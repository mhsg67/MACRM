package ca.usask.agents.macrm.node.agents

import ca.usask.agents.macrm.common.agents._
import ca.usask.agents.macrm.node.utils._
import akka.actor._

class NodeManagerAgent extends Agent {

    import context.dispatcher;
    val heartBeatEvent = context.system.scheduler.schedule(NodeConfig.heartBeatStartDelay, NodeConfig.heartBeatInterval, self, "heartBeatEvent")

    def receive = {
        case "initiateEvent"                           => Event_initiate()
        case "heartBeatEvent"                          => Event_heartBeat()
        case message: Message_ResourceRequest_RMAtoNMA => Handle_ResourceManagerAgent_ResourceRequest(message)
        case message: Message_ResourceRequest_AMAtoNMA => Handle_ApplicationMasterAgent_ResourceRequest(message)
        case _                                         => Handle_UnknownMessage
    }

    def Event_initiate() = {
        Logger.Log("NodeManagerAgent Initialization")
    }

    def Event_heartBeat() = {

    }

    def Handle_ResourceManagerAgent_ResourceRequest(message: Message_ResourceRequest_RMAtoNMA) = {

    }

    def Handle_ApplicationMasterAgent_ResourceRequest(message: Message_ResourceRequest_AMAtoNMA) = {

    }

}
