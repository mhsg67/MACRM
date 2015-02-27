package ca.usask.agents.macrm.resourcetracker.agents

import ca.usask.agents.macrm.resourcetracker.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import org.joda.time._
import akka.actor._

class ClusterDatabaseWriterAgent(val resourceTrackerAgent: ActorRef) extends Agent {

    def receive = {
        case "initiateEvent"     => Event_initiate()
        case message: _HeartBeat => Handle_HeartBeat(message)
        case _                   => Handle_UnknownMessage
    }

    def Event_initiate() = {
        Logger.Log("ClusterDatabaseWriterAgent Initialization")
    }

    def Handle_HeartBeat(message: _HeartBeat) = {
        val nodeId = new NodeId(message._source.path.address.host.get, message._source.path.address.port.get, message._source)
        val usedResources = message._report.containers.foldLeft(new Resource(0, 0))((x, y) => y.resource + x)
        ClusterDatabase.updateNodeState(nodeId, message._report.resource, usedResources, message._report.capabilities,
            message._report.utilization, message._report.queueState)
        ClusterDatabase.updateNodeContainer(nodeId, message._report.containers)
    }

}