package ca.usask.agents.macrm.clustermanager.agents

import ca.usask.agents.macrm.clustermanager.utils._
import ca.usask.agents.macrm.common.agents._
import ca.usask.agents.macrm.common.records._
import scala.collection.mutable._
import akka.actor._


class RackAgent(val myId: Int) extends Agent {

    var nodesWithFreeResources = Map[NodeId,Resource]()
    
    def receive = {
        case "initiateEvent" => Event_initiate()
        case message:_ServerWithEmptyResources => Handle_ServerWithEmptyResources(message)
        case _               => Handle_UnknownMessage
    }

    def Event_initiate() {
        Logger.Log("QueueAgent" + myId.toString() + " Initialization")
    }
    
    
    def Handle_ServerWithEmptyResources(message:_ServerWithEmptyResources) = {
         
    }
}