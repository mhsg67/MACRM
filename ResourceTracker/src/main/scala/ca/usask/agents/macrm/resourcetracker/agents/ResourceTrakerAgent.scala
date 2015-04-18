package ca.usask.agents.macrm.resourcetracker.agents

import ca.usask.agents.macrm.resourcetracker.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import org.joda.time._
import akka.actor._
import java.util.Formatter.DateTime

class RealResourceTrackerAgent extends Agent {

    var isInCentralizeState = false

    val clusterManagerAgent = context.actorSelection(ResourceTrakerConfig.getClusterManagerAddress())
    val clusterDatabaseReaderAgent = context.actorOf(Props(new ClusterDatabaseReaderAgent(self)), name = "ClusterDatabaseReaderAgent")
    val clusterDatabaseWriterAgent = context.actorOf(Props(new ClusterDatabaseWriterAgent(self)), name = "ClusterDatabaseWriterAgent")

    def receive = {
        case "initiateEvent"                => Event_initiate()
        case "changeToCentralizedMode" => Handle_changeToCentralizedMode()
        case message: _HeartBeat            => Handle_HeartBeat(message, sender())
        case message: _JMHeartBeat          => Handle_JMHeartBeat(message)
        case message: _ClusterState         => Handle_ClusterState(message)
        case message                        => Handle_UnknownMessage("ResourceTrackerAgent", message)
    }

    def Event_initiate() = {
        Logger.Log("ResourceTrackerAgent Initialization")

        clusterDatabaseWriterAgent ! "initiateEvent"
        clusterDatabaseReaderAgent ! "initiateEvent"
    }

    def Handle_ClusterState(message: _ClusterState) = {
        clusterManagerAgent ! message
    }

    def Handle_JMHeartBeat(message: _JMHeartBeat) = {
        clusterDatabaseReaderAgent ! message
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
            sender ! new _EmptyHeartBeatResponse(true)
        else
            sender ! new _EmptyHeartBeatResponse(false)
    }

    def doesServerHaveResourceForAJobManager(_nodeReport: NodeReport) = if (_nodeReport.getFreeResources() > ResourceTrakerConfig.minResourceForJobManager) true else false

    def addIPandPortToNodeReport(oldReport: NodeReport, sender: ActorRef) =
        new NodeReport(new NodeId(sender.path.address.host.get, sender.path.address.port.get, sender),
            oldReport.resource, oldReport.capabilities, oldReport.containers, oldReport.utilization,
            oldReport.nodeState, oldReport.queueState)
}
