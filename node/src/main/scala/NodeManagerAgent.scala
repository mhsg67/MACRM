package MACRM.node

import ca.usask.agents.macrm.common.agents._
import akka.actor._

class NodeManagerAgent extends Agent {
    def receive =
        {
            case message: Message_ResourceRequest_RMAtoNMA => Handle_ResourceManagerAgent_ResourceRequest(message)
            case _                                         => Handle_UnknownMessage
        }

    def Handle_ResourceManagerAgent_ResourceRequest(message: Message_ResourceRequest_RMAtoNMA) {

    }
}
