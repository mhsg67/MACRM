package MACRM.center

import akka.actor._
import MACRM.utility._
import org.joda.time._
import scala.collection.mutable._

class BasicClusterStateBlock(var source: ActorRef, var receiveTime: DateTime, var submitTime: DateTime, var node: Node)

/*
 * TODO: 
 *  0. list of node which are not available anymore(they have not sent heartbeat for a while)
 *  1. it should have a map of nodeId to clusterstateblock
 *  2. it should have a map of rackId to list of clusterstateblock of nodes in that rack
 *  3. it could have other ordering of cluster state such as 
 *      3.1 random selection of node with latest state and lowest resource utilizaiton
 *      3.2 
 */
object BasicClusterState {
    private var table = Map[String, BasicClusterStateBlock]()

    def GetStateOfNode(nodeId: String): BasicClusterStateBlock = table(nodeId)

    def GetStateOfRack(rackId: String): ListBuffer[BasicClusterStateBlock] = null
    
    def UpdateStateOfNode(source:ActorRef, receiveTime:DateTime, submitTime:DateTime, node:Node) = table 
}
