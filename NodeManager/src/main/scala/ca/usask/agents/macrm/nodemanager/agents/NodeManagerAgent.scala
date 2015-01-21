package ca.usask.agents.macrm.nodemanager.agents

import ca.usask.agents.macrm.common.agents._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.nodemanager.utils._
import org.joda.time.DateTime
import akka.actor._

class NodeManagerAgent extends Agent {

    import context.dispatcher;
    val heartBeatEvent = context.system.scheduler.schedule(NodeManagerConfig.heartBeatStartDelay, NodeManagerConfig.heartBeatInterval, self, "heartBeatEvent")

    val resourceTracker = context.actorSelection(NodeManagerConfig.getResourceTrackerAddress())

    def receive = {
        case "initiateEvent"  => Event_initiate()
        case "heartBeatEvent" => Event_heartBeat()
        case _                => Handle_UnknownMessage
    }

    def Event_initiate() = {
        Logger.Log("NodeManagerAgent Initialization")
    }

    def Event_heartBeat() = {
        val newNodeReport = createNodeReport()
        resourceTracker ! create_HeartBeat(newNodeReport)
    }

    def createNodeReport(): NodeReport = {
        return null
    }

    def create_HeartBeat(_nodeReport: NodeReport) = new _HeartBeat(self, DateTime.now(), _nodeReport)

}
