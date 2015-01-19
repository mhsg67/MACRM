package ca.usask.agents.macrm.resourcetracker.agents

import ca.usask.agents.macrm.common.agents._
import ca.usask.agents.macrm.resourcetracker.utils._
import akka.actor._

class ResourceTrackerAgent extends Agent {
    def receive = {
        case "initiateEvent"                           => Event_initiate()
        case message: Message_GiveClusterState_SAtoCSA => Handle_SchedulingAgent_ClusterStateRequest(message)
        case message: Message_HeartBeat_NMAtoRMA       => Handle_NodeManagerAgent_HeartBeat(message)
        case _                                         => Handle_UnknownMessage
    }

    def Event_initiate() = {
        Logger.Log("ResourceTrackerAgent Initialization")
    }

    def Handle_NodeManagerAgent_HeartBeat(message: Message_HeartBeat_NMAtoRMA) {

    }

    def Handle_SchedulingAgent_ClusterStateRequest(message: Message_GiveClusterState_SAtoCSA) {

    }

}
