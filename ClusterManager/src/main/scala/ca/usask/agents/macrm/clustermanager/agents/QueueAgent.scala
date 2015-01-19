package ca.usask.agents.macrm.clustermanager.agents

import ca.usask.agents.macrm.common.agents._
import akka.actor._
import scala.collection.mutable._

class QueueAgent extends Agent {

    def receive =
        {
            case message: Message_GiveNextResourceRequest_SAtoQA => HandleSchedulingContainerRequest(message)
            case message: Message_ResourceRequest_AMAtoRMA => Handle_ApplicationMasterAgent_ResourceRequest(message)
            case message: Message_ResourceRequest_CAtoRMA => Handle_ClientAgent_ResourceRequest(message)
            case _ => Handle_UnknownMessage
        }

    def Handle_ApplicationMasterAgent_ResourceRequest(message: Message_ResourceRequest_AMAtoRMA) {

    }

    def Handle_ClientAgent_ResourceRequest(message: Message_ResourceRequest_CAtoRMA) {

    }

    def HandleSchedulingContainerRequest(message: Message_GiveNextResourceRequest_SAtoQA) {

    }
}
