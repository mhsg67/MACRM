package ca.usask.agents.macrm.clustermanager.agents

import ca.usask.agents.macrm.clustermanager.utils._
import ca.usask.agents.macrm.common.agents._
import ca.usask.agents.macrm.common.records._
import scala.collection.mutable._
import akka.actor._
import scala.util.Random

class RackAgent(val myId: Int) extends Agent {

    var nodesWithFreeResources = Map[NodeId, Resource]()

    def receive = {

        case "initiateEvent" => Event_initiate()
        case "getNodeWithFreeResources" => Handle_getNodeWithFreeResources(sender())
        case message: _ServerWithEmptyResources => Handle_ServerWithEmptyResources(message)
        case message: _UpdateNodesWithFreeResourcesTransaction => Handle_updateNodesWithFreeResources(sender(), message)
        case message: _NodeWithFreeResourcesIsInconsistance => Handle_NodeWithFreeResourcesIsInconsistance(message)
        case message => Handle_UnknownMessage("RackAgent", message)
    }

    def Event_initiate() {
        Logger.Log("RackAgent" + myId.toString() + " Initialization")
    }

    def Handle_NodeWithFreeResourcesIsInconsistance(message: _NodeWithFreeResourcesIsInconsistance) = {
        message.nodes.foreach(x => nodesWithFreeResources.update(x._1, x._2))
    }

    def Handle_updateNodesWithFreeResources(sender: ActorRef, message: _UpdateNodesWithFreeResourcesTransaction) = {
        var impossibleIndexes = List[NodeId]()

        message.nodes.foreach(x => nodesWithFreeResources.get(x._1) match {
            case None => {
                println("unknown node")
                impossibleIndexes = x._1 :: impossibleIndexes
            }
            case Some(y) => {
                if (x._2.memory <= y.memory && x._2.virtualCore <= y.virtualCore)
                    updateResourceInNodesWithFreeResource(x._1, x._2)
                else{
                    println("nodes resource problem")
                    impossibleIndexes = x._1 :: impossibleIndexes
                }
            }
        })

        if (impossibleIndexes.isEmpty)
            sender ! "transactionCompleted"
        else{
            println("transactionConflicted")
            sender ! new _UnsuccessfulPartOfTrasaction(impossibleIndexes)
        }

    }

    def updateResourceInNodesWithFreeResource(nodeId: NodeId, occupied: Resource) = {
        val resource = nodesWithFreeResources.get(nodeId).get
        val newResource = new Resource(resource.memory - occupied.memory, resource.virtualCore - occupied.virtualCore)
        if (newResource.isNotUsable())
            nodesWithFreeResources.remove(nodeId)
        else
            nodesWithFreeResources.update(nodeId, newResource)
    }

    def Handle_getNodeWithFreeResources(sender: ActorRef) = {
        sender ! new _NodesWithFreeResources(Random.shuffle(nodesWithFreeResources.toList))
    }

    def Handle_ServerWithEmptyResources(message: _ServerWithEmptyResources) = {
        nodesWithFreeResources.update(message._report.nodeId, message._report.getFreeResources())
    }
}