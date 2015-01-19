package ca.usask.agents.macrm.jobmanager.agents

import ca.usask.agents.macrm.jobmanager.utils._
import ca.usask.agents.macrm.common.agents._
import akka.actor._

class JobManagerAgent extends Agent {
    def receive = {
        case "initiateEvent" => Event_initiate()
        case message: Message_ResourceRequestResponse_NMAtoAMA => Handle_NodeManagerAgent_ResponetoResourceRequest(message)
        case message: Message_ResourceRequestResponse_RMAtoCAorAMA => Handle_ResourceManagerAgent_ResponsetoResoureRequest(message)
        case _ => Handle_UnknownMessage
    }

    def Event_initiate() = {
        Logger.Log("ResourceTrackerAgent Initialization")
    }

    def Handle_ResourceManagerAgent_ResponsetoResoureRequest(message: Message_ResourceRequestResponse_RMAtoCAorAMA) {

    }

    def Handle_NodeManagerAgent_ResponetoResourceRequest(message: Message_ResourceRequestResponse_NMAtoAMA) {

    }
}
