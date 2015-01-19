package center

import scala.collection.mutable._
import org.scalatest._
import MACRM.center._
import MACRM.utility._

class BasicClusterStateTest extends UnitSpec {
    "ClusterState" should "Add new node to rackIdToNodeId map and Add nodeState to table" in {
        val bcs = BasicClusterState
        val testNode = new Node("TestNode", new Rack("TestRack", null), null, null, "127.0.0.1")
        bcs.UpdateStateOfNode(null, null, null, testNode)
        bcs.GetListOfNodeInRack("TestRack") should contain("TestNode")
        bcs.GetStateOfNode("TestNode") should be(testNode)
    }

    "ClusterState" should "Add not add node to rackIdToNodeId map but update nodeState in table" in {
        val bcs = BasicClusterState        
        val testNode = new Node("TestNode", new Rack("TestRack", null), null, null, "127.0.0.2")
        bcs.UpdateStateOfNode(null, null, null, testNode)     
        bcs.GetListOfNodeInRack("TestRack") should contain only("TestNode")
        
    }
}