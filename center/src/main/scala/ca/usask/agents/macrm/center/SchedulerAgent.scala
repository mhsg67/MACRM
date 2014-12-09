package ca.usask.agents.macrm.center

import ca.usask.agents.macrm.common.agents._

class SchedulerAgent extends Agent {
    def receive =
        {
            case message: Message_TakeNextResourceRequest_QAtoSA => Handle_QueueAgent_ResponsetoNextResourceRequest(message)
            case _ => Handle_UnknownMessage
        }

    def Handle_QueueAgent_ResponsetoNextResourceRequest(message: Message_TakeNextResourceRequest_QAtoSA) {

    }
}
