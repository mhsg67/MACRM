package ca.usask.agents.macrm.nodemanager.agents

import ca.usask.agents.macrm.common.agents._
import ca.usask.agents.macrm.jobmanager.agents._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.nodemanager.utils._
import org.joda.time.DateTime
import akka.actor._
import scala.concurrent.duration._
import scala.collection.mutable._
import scala.util._

class SimulationNodeManagerAgent(val id: Int = 0) extends Agent {

    val random = new Random(id)

    var heartBeatTimer: Cancellable = null
    var isInCentralizeState = false
    var pendingServingJob: Long = 0
    var havePendingServing = false
    var receivedHeartBeatRespond = true
    var missedHeartBeat = false
    var containerToActorSystem = Map[Long, ActorSystem]()
    var containerToOwnerActor = Map[Long, ActorRef]()

    val serverState = new SimulationServerState()
    val resourceTracker = context.actorSelection(NodeManagerConfig.getResourceTrackerAddress)

    import context.dispatcher

    def receive = {
        case "heartBeatEvent"                        => Event_heartBeat()
        case message: _EmptyHeartBeatResponse        => Handle_EmptyHeartBeatResponse(message)
        case message: _NodeManagerSimulationInitiate => Event_NodeManagerSimulationInitiate(message)
        case message: _ContainerExecutionFinished    => Event_ContainerExecutionFinished(message)
        case message: _ResourceSamplingInquiry       => Handle_ResourceSamplingInquiry(sender(), message)
        case message: _ResourceSamplingCancel        => Handle_ResourceSamplingCancel(message)
        case message: _AllocateContainerFromCM       => Handle_AllocateContainerFromCM(sender(), message)
        case message: _AllocateContainerFromJM       => Handle_AllocateContainerFromJM(sender(), message)
        case message: _AllocateContainerFromSA       => Handle_AllocateContainerFromSchedulerAgent(sender(), message)
        case message                                 => Handle_UnknownMessage("NodeManagerAgent", message)

    }

    def Event_NodeManagerSimulationInitiate(message: _NodeManagerSimulationInitiate) = {
        Logger.Log("NodeManagerAgent" + id.toString() + " Initialization Start")
        serverState.initializeSimulationServer(message.resource, message.capabilities)
        val startDelay = new FiniteDuration(NodeManagerConfig.heartBeatStartDelay + random.nextInt(NodeManagerConfig.heartBeatInterval), MILLISECONDS)
        heartBeatTimer = context.system.scheduler.scheduleOnce(startDelay, self, "heartBeatEvent")
    }

    def sendHeartBeat() = {
        missedHeartBeat = false
        resourceTracker ! new _HeartBeat(self, DateTime.now(), serverState.getServerStatus(self))
        val nextHeartBeatDelay = new FiniteDuration(NodeManagerConfig.heartBeatInterval, MILLISECONDS)
        heartBeatTimer = context.system.scheduler.scheduleOnce(nextHeartBeatDelay, self, "heartBeatEvent")
        receivedHeartBeatRespond = false
    }

    def Event_heartBeat() = {
        if (havePendingServing == false) sendHeartBeat() else missedHeartBeat = true
    }

    def Event_ContainerExecutionFinished(message: _ContainerExecutionFinished) = {
        val jobMangerRef = containerToOwnerActor.get(message.containerId).get

        serverState.killContainer(message.containerId) match {
            case None => ()
            case Some(x) =>
                if (message.isJobManager)
                    containerToActorSystem.get(message.containerId).get.stop(jobMangerRef)
                else
                    jobMangerRef ! _TasksExecutionFinished(self, DateTime.now(), x)
        }

        containerToOwnerActor.remove(message.containerId)

        if (isInCentralizeState) {
            heartBeatTimer.cancel()
            sendHeartBeat()
        }
    }

    def Handle_EmptyHeartBeatResponse(message: _EmptyHeartBeatResponse) = {
        receivedHeartBeatRespond = true
        isInCentralizeState = message._switchToCentralizeMode
    }

    def Handle_ResourceSamplingInquiry(sender: ActorRef, message: _ResourceSamplingInquiry) = {
        if (receivedHeartBeatRespond == true && havePendingServing == false && serverState.canAllocateThisSizeContainer(message._minRequiredResource)) {
            havePendingServing = true
            sender ! new _ResourceSamplingResponse(self, DateTime.now(), serverState.getServerFreeResources)
        }
    }

    def Handle_ResourceSamplingCancel(message: _ResourceSamplingCancel) = {
        havePendingServing = false
        if (missedHeartBeat) sendHeartBeat()
    }

    def Handle_AllocateContainerFromCM(sender: ActorRef, message: _AllocateContainerFromCM) = {
        receivedHeartBeatRespond = true

        if (message._jobDescriptions != null)
            if (startNewJobManagers(message._jobDescriptions) < message._jobDescriptions.length)
                sender ! "ridi"

        if (message._taskDescriptions != null)
            if (startNewTasks(message._taskDescriptions) < message._taskDescriptions.length)
                sender ! "ridi"
    }

    def Handle_AllocateContainerFromSchedulerAgent(sender: ActorRef, message: _AllocateContainerFromSA) = {
        isInCentralizeState = true

        if (message._jobDescriptions != null)
            if (startNewJobManagers(message._jobDescriptions) < message._jobDescriptions.length)
                sender ! new _ridi(serverState.getServerFreeResources)
            else
                sender ! "Scheduled"

        if (message._taskDescriptions != null)
            if (startNewTasks(message._taskDescriptions) < message._taskDescriptions.length)
                sender ! new _ridi(serverState.getServerFreeResources)
            else
                sender ! "Scheduled"
    }

    def startNewJobManagers(jobs: List[(JobDescription, SamplingInformation)]): Int = jobs match {
        case List() => 0
        case x :: xs =>
            if (startAJobManager(x._1, x._2))
                startNewJobManagers(xs) + 1
            else
                startNewJobManagers(xs)
    }

    def startAJobManager(job: JobDescription, samplInfo: SamplingInformation): Boolean = {
        serverState.createContainer(job.userId, job.jobId, 0, job.tasks(0).resource) match {
            case None => false
            case Some(x) => {
                createJobManagerActor(job, samplInfo, x)
                true
            }
        }
    }

    import com.typesafe.config.ConfigFactory
    def createJobManagerActor(job: JobDescription, samplInfo: SamplingInformation, containerId: Long) = {
        val jobMangerSystem = ActorSystem.create("JobManagerAgent", ConfigFactory.load().getConfig("JobManagerAgent"))
        val newJobManager = jobMangerSystem.actorOf(Props(new SimulationJobManagerAgent(containerId, self, job.userId, job.jobId, samplInfo)), name = "JobManagerAgent")
        newJobManager ! new _JobManagerSimulationInitiate(job.tasks)
        containerToOwnerActor.update(containerId, newJobManager)
        containerToActorSystem.update(containerId, jobMangerSystem)
    }

    def startNewTasks(tasks: List[TaskDescription]): Int = tasks match {
        case Nil     => 0
        case x :: xs => if (startANewTask(x, x.jobManagerRef)) startNewTasks(xs) + 1 else 0
    }

    def startANewTask(task: TaskDescription, ownerActor: ActorRef): Boolean = {
        serverState.createContainer(task.userId, task.jobId, task.index, task.resource) match {
            case None => {
                println(id + " taskResource:" + task.resource + " nodeResource:" + serverState.getServerFreeResources)
                false
            }
            case Some(x) => {
                containerToOwnerActor.update(x, ownerActor)
                context.system.scheduler.scheduleOnce(FiniteDuration(task.duration.getMillis, MILLISECONDS), self, new _ContainerExecutionFinished(x, false))
                true
            }
        }
    }

    def Handle_AllocateContainerFromJM(sender: ActorRef, message: _AllocateContainerFromJM) = {
        havePendingServing = false

        val tasks = message._taskDescriptions.map(x => new TaskDescription(sender, x.jobId, x.index, x.duration, x.resource, x.relativeSubmissionTime, x.constraints, x.userId))
        if (startNewTasks(tasks) < message._taskDescriptions.length)
            sender ! "ridi"

        if (missedHeartBeat) sendHeartBeat()
    }
}