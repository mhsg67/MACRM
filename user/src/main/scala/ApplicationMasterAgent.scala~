package MACRM.user

import ca.usask.agents.macrm.common._
import akka.actor._

class ApplicationMasterAgent extends Agent {
    def receive =
        {
            case message: Message_ResourceRequestResponse_NMAtoAMA => Handle_NodeManagerAgent_ResponetoResourceRequest(message)
            case message: Message_ResourceRequestResponse_RMAtoCAorAMA => Handle_ResourceManagerAgent_ResponsetoResoureRequest(message)
            case _ => Handle_UnknownMessage
        }

    def Handle_ResourceManagerAgent_ResponsetoResoureRequest(message: Message_ResourceRequestResponse_RMAtoCAorAMA) {

    }

    def Handle_NodeManagerAgent_ResponetoResourceRequest(message: Message_ResourceRequestResponse_NMAtoAMA) {

    }
}
