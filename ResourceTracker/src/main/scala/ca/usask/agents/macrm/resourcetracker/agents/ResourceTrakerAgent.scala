package ca.usask.agents.macrm.resourcetracker.agents

import ca.usask.agents.macrm.resourcetracker.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import org.joda.time._
import akka.actor._

class ResourceTrackerAgent extends Agent {

    val clusterManagerAgent = context.actorSelection(ResourceTrakerConfig.getQueueAgentAddress())
    val clusterDatabaseReaderAgent = context.actorOf(Props(new ClusterDatabaseReaderAgent(self)), name = "ClusterDatabaseReaderAgent")
    val clusterDatabaseWriterAgent = context.actorOf(Props(new ClusterDatabaseWriterAgent(self)), name = "ClusterDatabaseWriterAgent")

    def receive = {
        case "initiateEvent"                => Event_initiate()
        case "finishedCentralizeScheduling" => Handle_FinishedCentralizeScheduling()
        case message: _HeartBeat            => Handle_HeartBeat(message)
        case message: _JMHeartBeat          => Handle_JMHeartBeat(message)
        case _                              => Handle_UnknownMessage
    }

    def Event_initiate() = {
        Logger.Log("ResourceTrackerAgent Initialization")

        clusterDatabaseWriterAgent ! "initiateEvent"
        clusterDatabaseReaderAgent ! "initiateEvent"
    }

    def Handle_JMHeartBeat(message: _JMHeartBeat) = {
        clusterDatabaseWriterAgent ! message
        clusterDatabaseReaderAgent ! message
    }

    def Handle_HeartBeat(message: _HeartBeat) = {
        clusterDatabaseWriterAgent ! message
        if (doesServerHaveResourceForAJobManager(message._report))
            clusterManagerAgent ! new _ServerWithEmptyResources(self, DateTime.now(), addIPandPortToNodeReport(message._report, message._source))
    }

    def doesServerHaveResourceForAJobManager(_nodeReport: NodeReport) = if (_nodeReport.getAvailableResources() > ResourceTrakerConfig.minResourceForJobManager) true else false

    def addIPandPortToNodeReport(oldReport: NodeReport, sender: ActorRef) = new NodeReport(new NodeId(sender.path.address.host.get, sender.path.address.port.get, sender), oldReport.rackName, oldReport.used,
        oldReport.capability, oldReport.otherCapablity, oldReport.utilization, oldReport.reportTime, oldReport.nodeState, oldReport.nodeQueueState)

    def Handle_FinishedCentralizeScheduling() = {
        //TODO: Stop sending _ServerStatusUpdate
        //TODO: Start sending _ServerWithEmptyResources
    }
}
