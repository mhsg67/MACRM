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

    import context.dispatcher
    var currentWaveOfTasks = 0
    val resourceTracker = context.actorSelection(JobManagerConfig.getResourceTrackerAddress())
    val clusterManager = context.actorSelection(JobManagerConfig.getClusterManagerAddress())

    def receive = {
        case "initiateEvent"                        => Event_initiate()
        case "heartBeatEvent"                       => Event_heartBeat()
        case message: _ResourceSamplingResponse     => Handle_ResourceSamplingResponse(message, sender())
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

    def Event_JobManagerSimulationInitiate(message: _JobManagerSimulationInitiate) = {
        Logger.Log("JobManagerAgent<ID:" + jobId + "> Initialization")

        SamplingManager.loadSamplingInformation(samplingInformation)

        if (message.taskDescriptions.length > 1) {
            val tempTask = message.taskDescriptions.map(x => TaskDescription(self, jobId, x, userId))

            collection.SortedSet(tempTask.map(x => x.relativeSubmissionTime.getMillis): _*).foreach { x =>
                val tasksWithSimilarSubmissionTime = tempTask.filter(y => y.relativeSubmissionTime.getMillis == x)
                context.system.scheduler.scheduleOnce(FiniteDuration(x, MILLISECONDS), self, new _TaskSubmission(tasksWithSimilarSubmissionTime))
            }
        }
        else {
            //TODO:for jobs with just one task we should set the finish state and send it to RT
        }
    }

    def Event_heartBeat() = {
        resourceTracker ! create_JMHeartBeat()
        context.system.scheduler.scheduleOnce(JobManagerConfig.heartBeatInterval, self, "heartBeatEvent")
    }

    def Handle_ResourceSamplingResponse(message: _ResourceSamplingResponse, sender: ActorRef) =
        SamplingManager.whichTaskShouldSubmittedToThisNode(currentWaveOfTasks, message._availableResource, new NodeId(sender.path.address.host.get, sender.path.address.port.get, sender)) match {
            case None    => sender ! new _ResourceSamplingCancel(self, DateTime.now(), jobId)
            case Some(x) => sender ! new _AllocateContainerFromJM(self, DateTime.now(), x)
        }

    def Handle_TaskSubmission(message: _TaskSubmission) = {
        currentWaveOfTasks += 1
        if (currentWaveOfTasks == 1) context.system.scheduler.scheduleOnce(JobManagerConfig.heartBeatStartDelay, self, "heartBeatEvent")
        SamplingManager.addNewSubmittedTasksIntoWaveToTaks(currentWaveOfTasks, message.taskDescriptions)

        val samplingList = SamplingManager.getSamplingNode(message.taskDescriptions, 0)
        samplingList.foreach(x => getActorRefFromNodeId(x._1) ! new _ResourceSamplingInquiry(self, DateTime.now(), x._2, jobId))
        context.system.scheduler.scheduleOnce(JobManagerConfig.samplingTimeout, self, new _NodeSamplingTimeout(currentWaveOfTasks, 0))
    }

    def Handle_NodeSamplingTimeout(message: _NodeSamplingTimeout) = {
        val unscheduledTasks = SamplingManager.getUnscheduledTaskOfWave(message.forWave)
        if (unscheduledTasks.length > 0)
            if (message.retry < JobManagerConfig.numberOfAllowedSamplingRetry) {
                val samplingList = SamplingManager.getSamplingNode(unscheduledTasks, message.retry + 1)
                samplingList.foreach(x => getActorRefFromNodeId(x._1) ! new _ResourceSamplingInquiry(self, DateTime.now(), x._2, jobId))
                context.system.scheduler.scheduleOnce(JobManagerConfig.samplingTimeout, self, new _NodeSamplingTimeout(currentWaveOfTasks, message.retry + 1))
            }
            else {
                clusterManager ! new _TaskSubmissionFromJM(self, DateTime.now(), unscheduledTasks)
            }
    }

    def Handle_JMHeartBeatResponse(message: _JMHeartBeatResponse) = SamplingManager.loadNewSamplingRate(message._samplingRate)

    //TODO: change to refelect the number of retry and if
    //some of the task failed show their constraints
    def create_JMHeartBeat() = new _JMHeartBeat(self, DateTime.now(), new JobReport(userId, jobId))

    def getActorRefFromNodeId(node: NodeId): ActorSelection = context.actorSelection(JobManagerConfig.createNodeManagerAddressString(node.host, node.port))

}
