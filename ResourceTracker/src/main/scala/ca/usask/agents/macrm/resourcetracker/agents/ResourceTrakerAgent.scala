package ca.usask.agents.macrm.resourcetracker.agents

import ca.usask.agents.macrm.common.agents._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.resourcetracker.utils._
import akka.actor._
import org.joda.time.DateTime

class ResourceTrackerAgent extends Agent {

    val queueAgent = context.actorSelection(ResourceTrakerConfig.getQueueAgentAddress())

    def receive = {
        case "initiateEvent"     => Event_initiate()
        case message: _HeartBeat => Handle_HeartBeat(message)
        case _                   => Handle_UnknownMessage
    }

    def Event_initiate() = {
        Logger.Log("ResourceTrackerAgent Initialization")
    }

    def Handle_HeartBeat(message: _HeartBeat) {
        BasicClusterState.UpdateClusterState(message._source, message._time, message._report)
        if (doesServerHaveResourceForAJobManager(message._report))
            queueAgent ! create_ServerWithEmptyResources(message._report)
    }

    def doesServerHaveResourceForAJobManager(_nodeReport: NodeReport) = if ((_nodeReport.capability - (_nodeReport.used.foldLeft(new Resource(0, 0))((x, y) => x + y._2))) > ResourceTrakerConfig.minResourceForJobManager) true else false

    def create_ServerWithEmptyResources(_nodeReport: NodeReport) = new _ServerWithEmptyResources(self, DateTime.now(), _nodeReport)
}
