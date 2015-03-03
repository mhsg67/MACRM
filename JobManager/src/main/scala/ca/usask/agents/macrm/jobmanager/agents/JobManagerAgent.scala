package ca.usask.agents.macrm.jobmanager.agents

import ca.usask.agents.macrm.jobmanager.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import scala.collection.mutable._
import org.joda.time._
import akka.actor._
import scala.concurrent.duration._

class JobManagerAgent(val userId: Int,
                      val jobId: Long,
                      val samplingInformation: SamplingInformation) extends Agent {

    var currentWaveOfTasks = 0
    var waveToTasks = Map[Int, List[(Boolean, TaskDescription)]]()
    val resourceTracker = context.actorSelection(JobManagerConfig.getResourceTrackerAddress())
    val clusterManager = context.actorSelection(JobManagerConfig.getClusterManagerAddress())

    def receive = {
        case "initiateEvent"                        => Event_initiate()
        case "heartBeatEvent"                       => Event_heartBeat()
        case message: _TaskSubmission               => Handle_TaskSubmission(message)
        case message: _JMHeartBeatResponse          => Handle_JMHeartBeatResponse(message)
        case message: _NodeSamplingTimeout          => Handle_NodeSamplingTimeout(message)
        case message: _JobManagerSimulationInitiate => Event_JobManagerSimulationInitiate(message)
        case _                                      => Handle_UnknownMessage
    }

    def Event_initiate() = {
        Logger.Log("JobManagerAgent<ID:" + jobId + "> Initialization")

        SamplingManager.loadSamplingInformation(samplingInformation)
    }

    import context.dispatcher
    def Event_JobManagerSimulationInitiate(message: _JobManagerSimulationInitiate) = {
        Logger.Log("JobManagerAgent<ID:" + jobId + "> Initialization")

        SamplingManager.loadSamplingInformation(samplingInformation)

        collection.SortedSet(message.taskDescriptions.map(x => x.relativeSubmissionTime.getMillis): _*).foreach { x =>
            val tasksWithSimilarSubmissionTime = message.taskDescriptions.filter(y => y.relativeSubmissionTime.getMillis == x)
            context.system.scheduler.scheduleOnce(FiniteDuration(x, MILLISECONDS), self, new _TaskSubmission(tasksWithSimilarSubmissionTime))
        }
    }

    def Event_heartBeat() = {
        resourceTracker ! create_JMHeartBeat()
        context.system.scheduler.scheduleOnce(JobManagerConfig.heartBeatInterval, self, "heartBeatEvent")
    }

    def Handle_TaskSubmission(message: _TaskSubmission) = {
        currentWaveOfTasks += 1
        if (currentWaveOfTasks == 1) context.system.scheduler.scheduleOnce(JobManagerConfig.heartBeatStartDelay, self, "heartBeatEvent")
        addNewSubmittedTasksIntoWaveToTaks(currentWaveOfTasks, message.taskDescriptions)

        val samplingList = SamplingManager.getSamplingNode(message.taskDescriptions, false)
        samplingList.foreach(x => getActorRefFromNodeId(x._1) ! new _ResourceSamplingInquiry(self, DateTime.now(), x._2))
        context.system.scheduler.scheduleOnce(JobManagerConfig.samplingTimeout, self, new _NodeSamplingTimeout(currentWaveOfTasks, 0))
    }

    def Handle_NodeSamplingTimeout(message: _NodeSamplingTimeout) = {
        val unscheduledTasks = waveToTasks.get(message.forWave).get.filter(x => x._1 == false).map(y => y._2)
        if (unscheduledTasks.length > 0)
            if (message.retry < JobManagerConfig.numberOfAllowedSamplingRetry) {
                val samplingList = SamplingManager.getSamplingNode(unscheduledTasks, true)
                samplingList.foreach(x => getActorRefFromNodeId(x._1) ! new _ResourceSamplingInquiry(self, DateTime.now(), x._2))
                context.system.scheduler.scheduleOnce(JobManagerConfig.samplingTimeout, self, new _NodeSamplingTimeout(currentWaveOfTasks, message.retry + 1))
            }
            else {
                clusterManager ! new _TaskSubmissionFromJM(self, DateTime.now(), unscheduledTasks)
            }
    }

    def addNewSubmittedTasksIntoWaveToTaks(wave: Int, tasks: List[TaskDescription]) = {
        val tempTasks = tasks.map(x => new TaskDescription(self, jobId, x.index, x.duration, x.resource, x.relativeSubmissionTime, x.constraints, userId))
        waveToTasks.update(wave, tempTasks.map(x => (false, x)))
    }

    //TODO: change to refelect the number of retry and if
    //some of the task failed show their constraints
    def create_JMHeartBeat() = new _JMHeartBeat(self, DateTime.now(), new JobReport(userId, jobId))

    def createSamplingList(tasks: List[TaskDescription], retry: Boolean): List[(NodeId, Resource)] = SamplingManager.getSamplingNode(tasks, retry)

    def getActorRefFromNodeId(node: NodeId): ActorSelection = context.actorSelection(createNodeManagerAddressString(node.host, node.port))

    def createNodeManagerAddressString(host: String, port: Int) = "akka.tcp://NodeManagerAgent@" +
        host + ":" +
        port.toString() + "/" +
        "user/NodeManagerAgent"

    def Handle_JMHeartBeatResponse(message: _JMHeartBeatResponse) = SamplingManager.loadNewSamplingRate(message._samplingRate)

}
