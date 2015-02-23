package ca.usask.agents.macrm.resourcetracker.agents

import ca.usask.agents.macrm.common.agents._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.resourcetracker.utils._
import akka.actor._
import org.joda.time.DateTime

class ResourceTrackerAgent extends Agent {

    val clusterManagerAgent = context.actorSelection(ResourceTrakerConfig.getQueueAgentAddress())

    def receive = {
        case "initiateEvent"                => Event_initiate()
        case "finishedCentralizeScheduling" => Handle_FinishedCentralizeScheduling()
        case message: _HeartBeat            => Handle_HeartBeat(message)
        case _                              => Handle_UnknownMessage
    }

    def Event_initiate() = {
        Logger.Log("ResourceTrackerAgent Initialization")
    }

    def Handle_HeartBeat(message: _HeartBeat) {
        BasicClusterState.UpdateClusterState(message._source, message._time, message._report)             
        if (doesServerHaveResourceForAJobManager(message._report))            
            clusterManagerAgent ! new _ServerWithEmptyResources(self, DateTime.now(), addIPandPortToNodeReport(message._report, message._source))
    }

    def doesServerHaveResourceForAJobManager(_nodeReport: NodeReport) = if (_nodeReport.getAvailableResources() > ResourceTrakerConfig.minResourceForJobManager) true else false

    def addIPandPortToNodeReport(oldReport: NodeReport, sender:ActorRef) = new NodeReport(new NodeId(sender.path.address.host.get, sender.path.address.port.get, sender), oldReport.rackName, oldReport.used,
        oldReport.capability, oldReport.otherCapablity, oldReport.utilization, oldReport.reportTime, oldReport.nodeState, oldReport.nodeQueueState)

    def Handle_FinishedCentralizeScheduling() = {
        //TODO: Stop sending _ServerStatusUpdate
        //TODO: Start sending _ServerWithEmptyResources
    }
}
