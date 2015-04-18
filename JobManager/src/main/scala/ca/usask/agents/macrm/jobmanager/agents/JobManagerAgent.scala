package ca.usask.agents.macrm.jobmanager.agents

import ca.usask.agents.macrm.jobmanager.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import scala.collection.mutable._
import org.joda.time._
import akka.actor._
import scala.concurrent.duration._

class JobManagerAgentAdvance(val userId: Int,
                             val jobId: Long,
                             val samplingInformation: SamplingInformation) extends Agent {

    import context.dispatcher
    var samplingTimeout: Cancellable = null
    var waveToHighestSamplingRateWithTaskIndexWithConstraints = Map[Int, List[(Double, Int, List[Constraint])]]()
    var currentWaveOfTasks = 0
    val resourceTracker = context.actorSelection(JobManagerConfig.getResourceTrackerAddress)
    val clusterManager = context.actorSelection(JobManagerConfig.getClusterManagerAddress)
    val samplingTimeoutMillis = new FiniteDuration(JobManagerConfig.samplingTimeoutLong, MILLISECONDS)

    def receive = {
        case "initiateEvent"                    => Event_initiate()
        case message: _ResourceSamplingResponse => Handle_ResourceSamplingResponse(message, sender())
        case message: _TaskSubmission           => Handle_TaskSubmission(message)
        case message: _JMHeartBeatResponse      => Handle_JMHeartBeatResponse(message)
        case message: _NodeSamplingTimeout      => Handle_NodeSamplingTimeout(message)
        case message                            => Handle_UnknownMessage("JobManager_" + jobId.toString(), message)
    }

    def Event_initiate() = {
        Logger.Log("JobManagerAgent<ID:" + jobId + "> Initialization")
        SamplingManagerAdvance.loadSamplingInformation(samplingInformation)
    }

    def Handle_ResourceSamplingResponse(message: _ResourceSamplingResponse, sender: ActorRef) =
        SamplingManagerAdvance.whichTaskShouldSubmittedToThisNode(currentWaveOfTasks, message._availableResource, new NodeId(sender.path.address.host.get, sender.path.address.port.get, sender)) match {
            case None    => sender ! new _ResourceSamplingCancel(self, DateTime.now(), jobId)
            case Some(x) => sender ! new _AllocateContainerFromJM(self, DateTime.now(), x)
        }

    def Handle_TaskSubmission(message: _TaskSubmission) = {
        if (SamplingManagerAdvance.samplingRate == 0) {
            clusterManager ! new _TaskSubmissionFromJM(self, DateTime.now(), message.taskDescriptions)
            sendheartBeat(0)
        }
        else {
            currentWaveOfTasks += 1
            SamplingManagerAdvance.addNewSubmittedTasksIntoWaveToTaks(currentWaveOfTasks, message.taskDescriptions)

            val samplingList = SamplingManagerAdvance.getSamplingNode(message.taskDescriptions, 0)
            samplingList.foreach(x => getActorRefFromNodeId(x._1) ! new _ResourceSamplingInquiry(self, DateTime.now(), x._2, jobId))
            samplingTimeout = context.system.scheduler.scheduleOnce(samplingTimeoutMillis, self, new _NodeSamplingTimeout(currentWaveOfTasks, 0))
        }
    }

    def Handle_NodeSamplingTimeout(message: _NodeSamplingTimeout) = {
        val unscheduledTasks = SamplingManagerAdvance.getUnscheduledTaskOfWave(message.forWave)
        if (unscheduledTasks.length > 0) {
            updateWaveToHighestSamplingRateToConstraints(message.forWave, message.retry, unscheduledTasks)
            if (message.retry < JobManagerConfig.numberOfAllowedSamplingRetry) {
                val samplingList = SamplingManagerAdvance.getSamplingNode(unscheduledTasks, message.retry + 1)
                samplingList.foreach(x => getActorRefFromNodeId(x._1) ! new _ResourceSamplingInquiry(self, DateTime.now(), x._2, jobId))
                samplingTimeout = context.system.scheduler.scheduleOnce(samplingTimeoutMillis, self, new _NodeSamplingTimeout(currentWaveOfTasks, message.retry + 1))
            }
            else {
                clusterManager ! new _TaskSubmissionFromJM(self, DateTime.now(), unscheduledTasks)
            }
        }
        else
            sendheartBeat(message.forWave)
            
    }

    def updateWaveToHighestSamplingRateToConstraints(wave: Int, retry: Int, unscheduledTasks: List[TaskDescription]) = {
        val unscheduledTasksIndexAndConstraints = unscheduledTasks.map(x => (x.index, x.constraints))
        waveToHighestSamplingRateWithTaskIndexWithConstraints.get(wave) match {
            case None =>
                waveToHighestSamplingRateWithTaskIndexWithConstraints.update(wave, unscheduledTasksIndexAndConstraints.map(y => (retry * SamplingManagerAdvance.samplingRate, y._1, y._2)))
            case Some(x) => {
                val oldPart = x.filterNot(p => unscheduledTasksIndexAndConstraints.exists(q => q._1 == p._2))
                val newPart = unscheduledTasksIndexAndConstraints.map(x => (retry * SamplingManagerAdvance.samplingRate, x._1, x._2))
                waveToHighestSamplingRateWithTaskIndexWithConstraints.update(wave, oldPart ++ newPart)
            }
        }
    }

    def Handle_JMHeartBeatResponse(message: _JMHeartBeatResponse) = SamplingManagerAdvance.loadNewSamplingRate(message._samplingRate)

    def sendheartBeat(waveNumber: Int) = resourceTracker ! create_JMHeartBeat()

    def create_JMHeartBeat() = new _JMHeartBeat(self, DateTime.now(), new JobReport(userId, jobId, SamplingManagerAdvance.samplingRate))

    def getActorRefFromNodeId(node: NodeId): ActorSelection = context.actorSelection(JobManagerConfig.createNodeManagerAddressString(node.host, node.port))

}
