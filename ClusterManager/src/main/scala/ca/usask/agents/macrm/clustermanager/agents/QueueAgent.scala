package ca.usask.agents.macrm.clustermanager.agents

import ca.usask.agents.macrm.clustermanager.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime
import akka.actor._
import java.util.Formatter.DateTime

/**
 * TODO: For adaption part which switch to central decision making
 * create some scheduler actors and forward resource request for allocation to them
 */
class QueueAgent extends Agent {

    val schedulingQueue = AbstractQueue(ClusterManagerConfig.QueueType)
    val clusterStructure = new ClusterDatastructure()

    def receive = {
        case "initiateEvent"                    => Event_initiate()
        case "getNextTaskForScheduling"         => Handle_getNextTaskForScheduling(sender)
        case message: _ClusterState             => Handle_ClusterState(message)
        case message: _ServerWithEmptyResources => Handle_ServerWithEmptyResources(message)
        case message: _JobSubmission            => Handle_JobSubmission(message)
        case message: _TaskSubmission           => Handle_TaskSubmission(message)
        case message                            => Handle_UnknownMessage("QueueAgent", message)
    }

    def Event_initiate() = {
        Logger.Log("QueueAgent Initialization")
    }

    def Handle_ServerWithEmptyResources(message: _ServerWithEmptyResources) = {
        val freeResources = message._report.getFreeResources()
        if (!schedulingQueue.isEmpty && !freeResources.isNotUsable())
            schedulingQueue.getBestMatches(freeResources, message._report.capabilities) match {
                case None => message._report.nodeId.agent ! "emptyHeartBeatResponse"
                case Some(x) => {
                    val addedSamplingInfo = x._1.map(y => {
                        if (y.numberOfTasks != 1)
                            (y, clusterStructure.getCurrentSamplingInformation(y.constraints()))
                        else
                            (y, null)
                    })
                    allocateContainer(x._2, addedSamplingInfo, message)
                }
            }
        else
            message._report.nodeId.agent ! new _EmptyHeartBeatResponse(false)
    }

    def allocateContainer(tasks: List[TaskDescription], jobs: List[(JobDescription, SamplingInformation)], message: _ServerWithEmptyResources) =
        message._report.nodeId.agent ! new _AllocateContainerFromCM(self, DateTime.now(), tasks, jobs, false)

    def Handle_JobSubmission(message: _JobSubmission) = schedulingQueue.EnqueueRequest(message.jobDescription)

    def Handle_TaskSubmission(message: _TaskSubmission) = message.taskDescriptions.foreach(x => schedulingQueue.EnqueueRequest(x))

    def Handle_ClusterState(message: _ClusterState) = clusterStructure.updateClusterStructure(message._newSamplingRate, message._removedServers, message._addedServers, message._rareResources)

    def Handle_getNextTaskForScheduling(sender: ActorRef) = sender ! new _headOfSchedulingQueue(schedulingQueue.DequeueRequest())

}
