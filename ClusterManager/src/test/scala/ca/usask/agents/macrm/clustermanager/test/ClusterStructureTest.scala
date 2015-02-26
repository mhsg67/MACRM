package ca.usask.agents.macrm.clustermanager.test

import ca.usask.agents.macrm.clustermanager.utils._
import ca.usask.agents.macrm.common.records._
import org.joda.time._

class ClusterStructureTest extends UnitSpec {

    "updateClusterStructure" should "change newSamplingRate && add new Servers && remove some Servers" in {

        val nId1 = NodeId("0.0.0.1", 1)
        val nId2 = NodeId("0.0.0.1", 2)
        val nId3 = NodeId("0.0.0.1", 3)

        val n1 = new NodeReport(nId1, "R1", null, new Resource(2, 1000), null, null, DateTime.now, NodeState("NEW"), null)
        val n2 = new NodeReport(nId2, "R1", null, new Resource(2, 1000), null, null, DateTime.now, NodeState("NEW"), null)
        val n3 = new NodeReport(nId3, "R1", null, new Resource(2, 1000), null, null, DateTime.now, NodeState("NEW"), null)
        val n4 = new NodeReport(NodeId("0.0.0.2", 5), "R2", null, new Resource(2, 1000), null, null, DateTime.now, NodeState("NEW"), null)
        val n5 = new NodeReport(NodeId("0.0.0.2", 1), "R2", null, new Resource(2, 1000), null, null, DateTime.now, NodeState("NEW"), null)
        val n6 = new NodeReport(NodeId("0.0.0.3", 4), "R3", null, new Resource(2, 1000), null, null, DateTime.now, NodeState("NEW"), null)
        val newN1 = new NodeReport(nId1, "R5", null, new Resource(4, 2000), null, null, DateTime.now, NodeState("NEW"), null)
        val newN4 = new NodeReport(NodeId("0.0.0.2", 5), "R2", null, new Resource(8, 8000), null, null, DateTime.now, NodeState("NEW"), null) 
        
        val cs = new ClusterStructure()

        cs.updateClusterStructure(-1, null, null)
        cs.samplingRate should be(2)
        cs.clusterNodes should be(List())

        cs.updateClusterStructure(4, null, null)
        cs.samplingRate should be(4)
        cs.clusterNodes should be(List())

        cs.updateClusterStructure(-1, null, List(n1, n2, n3, n4))
        cs.samplingRate should be(4)
        cs.clusterNodes.length should be(4)
        cs.clusterNodes(0).nodeId.host should be("0.0.0.1")

        cs.updateClusterStructure(-1, List(nId2), null)
        cs.samplingRate should be (4)
        cs.clusterNodes.length should be (3)
        cs.clusterNodes should not contain (n2) 
        
        cs.updateClusterStructure(8, null, List(newN1))
        cs.samplingRate should be (8)
        cs.clusterNodes.length should be(3)
        cs.clusterNodes should not contain (n1)               
        
        cs.updateClusterStructure(-1, List(nId3), List(n5,n6,newN4))
        cs.samplingRate should be (8)
        cs.clusterNodes.length should be (4)
        cs.clusterNodes should not contain(n4)        
        cs.clusterNodes should not contain(n3)
        cs.clusterNodes should contain (n6)        
    }

}