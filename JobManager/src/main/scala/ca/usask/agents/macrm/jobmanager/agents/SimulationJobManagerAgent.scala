package ca.usask.agents.macrm.jobmanager.agents

import ca.usask.agents.macrm.jobmanager.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import scala.collection.mutable._
import org.joda.time._
import akka.actor._
import scala.concurrent.duration._

class SimulationJobManagerAgent(val containerId: Long,
                                val node: ActorRef,
                                val userId: Int,
                                val jobId: Long,
                                val samplingInformation: SamplingInformation) extends Agent {

    var waveArrival: DateTime = null
    var samplingTimeout: Cancellable = null
    var currentWaveOfTasks = 0
    var remainingTasksToFinish = 0

    val waveToSamplingRate = Map[Int, Int]()
    val samplingManager = new SimulationSamplingManager()
    val resourceTracker = context.actorSelection(JobManagerConfig.getResourceTrackerAddress)
    val clusterManager = context.actorSelection(JobManagerConfig.getClusterManagerAddress())

    import context.dispatcher
    def receive = {
        case message: _ResourceSamplingResponse     => Handle_ResourceSamplingResponse(message, sender())
        case message: _TaskSubmission               => Handle_TaskSubmission(message)
        case message: _JMHeartBeatResponse          => Handle_JMHeartBeatResponse(message)
        case message: _NodeSamplingTimeout          => Handle_NodeSamplingTimeout(message)
        case message: _TasksExecutionFinished       => Handle_TasksExecutionFinished(message)
        case message: _JobManagerSimulationInitiate => Event_JobManagerSimulationInitiate(message)
        case message                                => Handle_UnknownMessage("JobManager_" + jobId.toString(), message)

    }

    def updateWaveToSamplingRate(wave: Int, retry: Int) =
        waveToSamplingRate.update(wave, (samplingManager.samplingRate * math.pow(2, retry)).toInt)

    def Handle_JMHeartBeatResponse(message: _JMHeartBeatResponse) =
        samplingManager.loadNewSamplingRate(message._samplingRate)

    def getActorRefFromNodeId(node: NodeId): ActorSelection =
        context.actorSelection(JobManagerConfig.createNodeManagerAddressString(node.host, node.port))

    def Event_JobManagerSimulationInitiate(message: _JobManagerSimulationInitiate) = {
        samplingManager.loadSamplingInformation(samplingInformation)

        val tempTask = message.taskDescriptions.tail.map(x => TaskDescription(self, jobId, x, userId))
        remainingTasksToFinish = tempTask.length
        var waveCount = 0
        collection.SortedSet(tempTask.map(x => x.relativeSubmissionTime.getMillis): _*).
            foreach { x =>
                waveCount += 1
                val tasksWithSimilarSubmissionTime = tempTask.filter(y => y.relativeSubmissionTime.getMillis == x)
                context.system.scheduler.scheduleOnce(x millis, self, new _TaskSubmission(tasksWithSimilarSubmissionTime))
            }
        waveArrival = DateTime.now()
        println(jobId + " WC " + waveCount)
    }

    def Handle_ResourceSamplingResponse(message: _ResourceSamplingResponse, sender: ActorRef) = {
        samplingManager.whichTaskShouldSubmittedToThisNode(message._availableResource) match {
            case None => {
                sender ! new _ResourceSamplingCancel(self, DateTime.now(), jobId)
            }
            case Some(x) => {
                sender ! new _AllocateContainerFromJM(self, DateTime.now(), x)
            }
        }
    }

    def Handle_TaskSubmission(message: _TaskSubmission) = {
        if (samplingManager.samplingRate == 0) {
            clusterManager ! new _TaskSubmissionFromJM(self, DateTime.now(), message.taskDescriptions)
            sendheartBeat(-1)
        }
        else {
            println(jobId + " WA " + (DateTime.now().getMillis - waveArrival.getMillis))
            currentWaveOfTasks += 1
            updateWaveToSamplingRate(currentWaveOfTasks, 0)
            samplingManager.addNewSubmittedTasks(message.taskDescriptions)
            val samplingList = samplingManager.getSamplingNode(message.taskDescriptions, 0)

            samplingList.foreach(x => getActorRefFromNodeId(x._1) ! new _ResourceSamplingInquiry(self, DateTime.now(), x._2, jobId))
            samplingTimeout = context.system.scheduler.scheduleOnce(JobManagerConfig.samplingTimeoutLong millis, self, new _NodeSamplingTimeout(currentWaveOfTasks, 1))
        }
    }

    def Handle_NodeSamplingTimeout(message: _NodeSamplingTimeout) = {
        val unscheduledTasks = samplingManager.getUnscheduledTaskOfWave(message.forWave)
        if (unscheduledTasks.length > 0) {
            updateWaveToSamplingRate(message.forWave, message.retry)
            if (message.retry <= JobManagerConfig.numberOfAllowedSamplingRetry) {
                val samplingList = samplingManager.getSamplingNode(unscheduledTasks, message.retry)
                samplingList.foreach(x => getActorRefFromNodeId(x._1) ! new _ResourceSamplingInquiry(self, DateTime.now(), x._2, jobId))
                samplingTimeout = context.system.scheduler.scheduleOnce(JobManagerConfig.samplingTimeoutLong millis, self, new _NodeSamplingTimeout(currentWaveOfTasks, message.retry + 1))

                println("SampTimeout:" + jobId.toString() + " SampRate:" + (samplingList.length / unscheduledTasks.length).toString() + " SampListSize:" + samplingList.length.toString())
            }
            else {
                sendheartBeat(message.forWave)
                clusterManager ! new _TaskSubmissionFromJM(self, DateTime.now(), unscheduledTasks)
            }
        }
        else {
            println(jobId + " WTO " + (DateTime.now().getMillis - waveArrival.getMillis))
            sendheartBeat(message.forWave)
        }
    }

    def Handle_TasksExecutionFinished(message: _TasksExecutionFinished) = {
        remainingTasksToFinish -= 1
        if (remainingTasksToFinish == 0) {
            if (samplingTimeout != null) samplingTimeout.cancel()
            clusterManager ! new _JobFinished(self, DateTime.now(), jobId)
            context.system.scheduler.scheduleOnce(2 millis, node, new _ContainerExecutionFinished(containerId, true))
        }
    }

    def sendheartBeat(waveNumber: Int) = {
        if (waveNumber != -1)
            resourceTracker ! new _JMHeartBeat(self, DateTime.now(), new JobReport(userId, jobId, waveToSamplingRate.get(waveNumber).get))
        else
            resourceTracker ! new _JMHeartBeat(self, DateTime.now(), new JobReport(userId, jobId, 0))
    }
}
