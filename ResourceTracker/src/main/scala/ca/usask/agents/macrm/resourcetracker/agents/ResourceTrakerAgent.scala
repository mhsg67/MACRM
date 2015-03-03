package ca.usask.agents.macrm.resourcetracker.agents

import ca.usask.agents.macrm.resourcetracker.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import org.joda.time._
import akka.actor._
import java.util.Formatter.DateTime

class ResourceTrackerAgent extends Agent {

    var hasSubmittedFirstClusterState = false

    val clusterManagerAgent = context.actorSelection(ResourceTrakerConfig.getQueueAgentAddress())
    val clusterDatabaseReaderAgent = context.actorOf(Props(new ClusterDatabaseReaderAgent(self)), name = "ClusterDatabaseReaderAgent")
    val clusterDatabaseWriterAgent = context.actorOf(Props(new ClusterDatabaseWriterAgent(self)), name = "ClusterDatabaseWriterAgent")

    def receive = {
        case "initiateEvent"                => Event_initiate()
        case "finishedCentralizeScheduling" => Handle_FinishedCentralizeScheduling()
        case message: _HeartBeat            => Handle_HeartBeat(message)
        case message: _JMHeartBeat          => Handle_JMHeartBeat(message)
        case message: _ClusterState         => Handle_ClusterState(message)
        case _                              => Handle_UnknownMessage("ResourceTrackerAgent")
    }

    def Event_initiate() = {
        Logger.Log("ResourceTrackerAgent Initialization")

        clusterDatabaseWriterAgent ! "initiateEvent"
        clusterDatabaseReaderAgent ! "initiateEvent"
    }

    def Handle_ClusterState(message: _ClusterState) = {
        hasSubmittedFirstClusterState = true
        clusterManagerAgent ! message
    }

    def Handle_JMHeartBeat(message: _JMHeartBeat) = {
        clusterDatabaseReaderAgent ! message
    }

    def Handle_HeartBeat(message: _HeartBeat) = {
        clusterDatabaseWriterAgent ! message        
        if (doesServerHaveResourceForAJobManager(message._report) && hasSubmittedFirstClusterState)
            clusterManagerAgent ! new _ServerWithEmptyResources(self, DateTime.now(), addIPandPortToNodeReport(message._report, message._source))
    }

    def doesServerHaveResourceForAJobManager(_nodeReport: NodeReport) = if (_nodeReport.getFreeResources() > ResourceTrakerConfig.minResourceForJobManager) true else false

    def addIPandPortToNodeReport(oldReport: NodeReport, sender: ActorRef) =
        new NodeReport(new NodeId(sender.path.address.host.get, sender.path.address.port.get, sender),
            oldReport.resource, oldReport.capabilities, oldReport.containers, oldReport.utilization,
            oldReport.nodeState, oldReport.queueState)

    def Handle_FinishedCentralizeScheduling() = {
        //TODO: Stop sending _ServerStatusUpdate
        //TODO: Start sending _ServerWithEmptyResources
    }
}
