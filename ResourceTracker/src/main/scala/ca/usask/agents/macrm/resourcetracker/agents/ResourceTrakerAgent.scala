package ca.usask.agents.macrm.resourcetracker.agents

import ca.usask.agents.macrm.resourcetracker.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import org.joda.time._
import akka.actor._
import java.util.Formatter.DateTime

class ResourceTrackerAgent extends Agent {

    var isInCentralizeState = false

    val clusterManagerAgent = context.actorSelection(ResourceTrakerConfig.getClusterManagerAddress())
    val clusterDatabaseReaderAgent = context.actorOf(Props(new ClusterDatabaseReaderAgent(self)), name = "ClusterDatabaseReaderAgent")
    val clusterDatabaseWriterAgent = context.actorOf(Props(new ClusterDatabaseWriterAgent(self)), name = "ClusterDatabaseWriterAgent")

    def addIPandPortToNodeReport(oldReport: NodeReport, sender: ActorRef) =
        new NodeReport(new NodeId(sender.path.address.host.get, sender.path.address.port.get, sender),
            oldReport.resource, oldReport.capabilities, oldReport.containers, oldReport.utilization,
            oldReport.nodeState, oldReport.queueState)

    def Handle_ClusterState(message: _ClusterState) =
        clusterManagerAgent ! message

    def Handle_JMHeartBeat(message: _JMHeartBeat) =
        clusterDatabaseReaderAgent ! message

    def receive = {
        case "initiateEvent"           => Event_initiate()
        case "changeToCentralizedMode" => Handle_changeToCentralizedMode()
        case message: _HeartBeat       => Handle_HeartBeat(message, sender())
        case message: _JMHeartBeat     => Handle_JMHeartBeat(message)
        case message: _ClusterState    => Handle_ClusterState(message)
        case message                   => Handle_UnknownMessage("ResourceTrackerAgent", message)
    }

    def Event_initiate() = {
        Logger.Log("ResourceTrackerAgent Initialization")

        clusterDatabaseWriterAgent ! "initiateEvent"
        clusterDatabaseReaderAgent ! "initiateEvent"
    }

    def Handle_changeToCentralizedMode() = {
        isInCentralizeState = true
        clusterManagerAgent ! "changeToCentralizedMode"
    }

    def Handle_HeartBeat(oldMessage: _HeartBeat, sender: ActorRef) = {
        val message = new _HeartBeat(sender, oldMessage._time, oldMessage._report)
        clusterDatabaseWriterAgent ! message

        if (doesServerHaveResourceForAJobManager(message._report))
            clusterManagerAgent ! new _ServerWithEmptyResources(self, DateTime.now(), addIPandPortToNodeReport(message._report, sender))
        else if (isInCentralizeState == true)
            sender ! new _EmptyHeartBeatResponse(1)
        else
            sender ! new _EmptyHeartBeatResponse(0)
    }

    def doesServerHaveResourceForAJobManager(_nodeReport: NodeReport) = {
        val nodeFreeResources = _nodeReport.getFreeResources()
        if (nodeFreeResources.memory >= ResourceTrakerConfig.minResourceForJobManager.memory &&
            nodeFreeResources.virtualCore >= ResourceTrakerConfig.minResourceForJobManager.virtualCore)
            true
        else
            false
    }
}
