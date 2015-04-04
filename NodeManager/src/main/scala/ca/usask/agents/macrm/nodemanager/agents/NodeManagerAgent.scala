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

class NodeManagerAgent(val id: Int = 0) extends Agent {

    val random = new Random(id)
    var havePendingServing = false
    var receivedHeartBeatRespond = false
    var missedHeartBeat = false
    var containerToActorSystem = Map[Long, ActorSystem]()
    var containerToOwnerActor = Map[Long, ActorRef]()

    val serverState = new SimulationServerState()
    val resourceTracker = context.actorSelection(NodeManagerConfig.getResourceTrackerAddress)

    import context.dispatcher

    def receive = {
        case "heartBeatEvent"                        => Event_heartBeat()
        case "emptyHeartBeatResponse"                => receivedHeartBeatRespond = true
        case message: _NodeManagerSimulationInitiate => Event_NodeManagerSimulationInitiate(message)
        case message: _ContainerExecutionFinished => {
            //println("ContainerExecutionFinished: " + message.containerId)
            Event_ContainerExecutionFinished(message)
        }
        case message: _ResourceSamplingInquiry => {
            //println("ResourceSamplingInquiry " + id.toString())
            Handle_ResourceSamplingInquiry(sender(), message)
        }
        case message: _ResourceSamplingCancel => {
            //println("ResourceSamplingCancel " + id.toString())
            Handle_ResourceSamplingCancel(message)
        }
        case message: _AllocateContainerFromCM => {
            //println("AllocateContainerFromCM " + id.toString())
            Handle_AllocateContainerFromCM(sender(), message)
        }
        case message: _AllocateContainerFromJM => {
            //println("AllocateContainerFromJM " + id.toString() + " #Containers" + message._taskDescriptions.length)
            Handle_AllocateContainerFromJM(sender(), message)
        }
        case _ => Handle_UnknownMessage("NodeManagerAgent")
    }

    def Event_NodeManagerSimulationInitiate(message: _NodeManagerSimulationInitiate) = {
        Logger.Log("NodeManagerAgent" + id.toString() + " Initialization Start")
        serverState.initializeSimulationServer(message.resource, message.capabilities)
        val startDelay = new FiniteDuration(NodeManagerConfig.heartBeatStartDelay + random.nextInt(NodeManagerConfig.heartBeatInterval), MILLISECONDS)
        context.system.scheduler.scheduleOnce(startDelay, self, "heartBeatEvent")
        Logger.Log("NodeManagerAgent" + id.toString() + " Initialization End")
    }

    def sendHeartBeat() = {
        missedHeartBeat = false
        resourceTracker ! new _HeartBeat(self, DateTime.now(), serverState.getServerStatus(self))
        val nextHeartBeatDelay = new FiniteDuration(NodeManagerConfig.heartBeatInterval, MILLISECONDS)
        context.system.scheduler.scheduleOnce(nextHeartBeatDelay, self, "heartBeatEvent")
        receivedHeartBeatRespond = false
    }

    def Event_heartBeat() = {
        if (!havePendingServing) sendHeartBeat() else missedHeartBeat = true
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
    }

    def Handle_ResourceSamplingInquiry(sender: ActorRef, message: _ResourceSamplingInquiry) = {
        if (receivedHeartBeatRespond == true && havePendingServing == false && serverState.getServerFreeResources > message._minRequiredResource) {
            havePendingServing == true
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
        val newJobManager = jobMangerSystem.actorOf(Props(new JobManagerAgent(containerId, self, job.userId, job.jobId, samplInfo)), name = "JobManagerAgent")
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
            case None => false
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