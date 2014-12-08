package MACRM.finder

import ca.usask.agents.macrm.common._
import akka.actor._

class ResourceFinderAgent extends Agent {
    def receive =
        {
            case message: Message_GiveClusterState_SAtoCSA => Handle_SchedulingAgent_ClusterStateRequest(message)
            case message: Message_HeartBeat_NMAtoRMA       => Handle_NodeManagerAgent_HeartBeat(message)
            case _                                         => Handle_UnknownMessage
        }

    def Handle_NodeManagerAgent_HeartBeat(message: Message_HeartBeat_NMAtoRMA) {

    }

    def Handle_SchedulingAgent_ClusterStateRequest(message: Message_GiveClusterState_SAtoCSA) {

    }
    
    
}
