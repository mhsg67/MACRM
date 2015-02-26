package ca.usask.agents.macrm.resourcetracker.utils

import akka.actor._
import ca.usask.agents.macrm.common.records._
import org.joda.time._
import scala.collection.mutable._


class BasicClusterStateBlock(var source: ActorRef, var receiveTime: DateTime, var submitTime: DateTime, var node: NodeReport)

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
    
    def updateClusterDatabase() = {
        
    }
    
    def UpdateClusterState(_source:ActorRef, _receiveTime: DateTime, _nodeReport:NodeReport) = {
        
    }
    
    /*
     * old Part
     */
    private var table = Map[NodeId, BasicClusterStateBlock]()
    private var rackIdToNodesId = Map[String, MutableList[NodeId]]()

    def GetStateOfNode(_nodeId: NodeId): BasicClusterStateBlock = table(_nodeId)

    def GetListOfNodeInRack(_rackId: String): MutableList[NodeId] = rackIdToNodesId(_rackId)

    def GetStateOfNodeInRack(_rackId: String): MutableList[BasicClusterStateBlock] = {
        var result = MutableList[BasicClusterStateBlock]()
        rackIdToNodesId(_rackId).foreach { x => result += table(x) }
        return result    
    }

    
    def UpdateStateOfNode(_source: ActorRef, _receiveTime: DateTime, _submitTime: DateTime, _node: NodeReport): Unit = {
        if (!rackIdToNodesId(_node.rackName).contains(_node.nodeId))
            rackIdToNodesId(_node.rackName) += _node.nodeId
        table(_node.nodeId).source = _source
        table(_node.nodeId).receiveTime = _receiveTime
        table(_node.nodeId).submitTime = _submitTime
        table(_node.nodeId).node = _node
    }

    def IsAvailableNode(_nodeId: NodeId): Boolean = {
        var currentTime = DateTime.now()
        var lastHeartBeatTime = table(_nodeId).submitTime

        if (currentTime.getMillis() - lastHeartBeatTime.getMillis() > ResourceTrakerConfig.heatbeatTimeOut)
            return false
        else
            return true
    }
}
