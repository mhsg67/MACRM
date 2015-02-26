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

        cs.updateClusterStructure(-1, null, null, null)
        cs.samplingRate should be(2)
        cs.clusterNodes should be(List())

        cs.updateClusterStructure(4, null, null, null)
        cs.samplingRate should be(4)
        cs.clusterNodes should be(List())

        cs.updateClusterStructure(-1, null, List(n1, n2, n3, n4), null)
        cs.samplingRate should be(4)
        cs.clusterNodes.length should be(4)
        cs.clusterNodes(0).nodeId.host should be("0.0.0.1")

        cs.updateClusterStructure(-1, List(nId2), null, null)
        cs.samplingRate should be(4)
        cs.clusterNodes.length should be(3)
        cs.clusterNodes should not contain (n2)

        cs.updateClusterStructure(8, null, List(newN1), null)
        cs.samplingRate should be(8)
        cs.clusterNodes.length should be(3)
        cs.clusterNodes should not contain (n1)

        cs.updateClusterStructure(-1, List(nId3), List(n5, n6, newN4), null)
        cs.samplingRate should be(8)
        cs.clusterNodes.length should be(4)
        cs.clusterNodes should not contain (n4)
        cs.clusterNodes should not contain (n3)
        cs.clusterNodes should contain(n6)
    }

    "doseCapabilityMatchConstraint" should "return right boolean value" in {

        val co1 = new Constraint("1", 0, 1)
        val co2 = new Constraint("1", 1, 1)
        val co3 = new Constraint("1", 2, 4)
        val co4 = new Constraint("1", 3, 0)
        val co5 = new Constraint("1", 2, 1)
        val co6 = new Constraint("1", 3, 3)

        val cp1 = new Constraint("1", 0, 1)
        val cs = new ClusterStructure()

        var result = cs.doseCapabilityMatchConstraint(cp1, co1)
        result should be(true)

        result = cs.doseCapabilityMatchConstraint(cp1, co2)
        result should be(false)

        result = cs.doseCapabilityMatchConstraint(cp1, co3)
        result should be(true)

        result = cs.doseCapabilityMatchConstraint(cp1, co4)
        result should be(true)

        result = cs.doseCapabilityMatchConstraint(cp1, co5)
        result should be(false)

        result = cs.doseCapabilityMatchConstraint(cp1, co6)
        result should be(false)

        result = cs.doseCapabilityMatchConstraint(cp1, co1)
        result should be(true)
    }

    "getMatchCapablitiesOfNode" should "return list of node with related capablity" in {

        val co1 = new Constraint("1", 0, 1)
        val co2 = new Constraint("1", 0, 2)
        val co3 = new Constraint("2", 0, 3)
        val co4 = new Constraint("2", 0, 3)
        val co5 = new Constraint("3", 0, 5)
        val co6 = new Constraint("3", 0, 6)
        val co7 = new Constraint("4", 0, 0)
        val co8 = new Constraint("5", 0, 1)

        val cp1 = new Constraint("1", 1, 1)
        val cp2 = new Constraint("2", 2, 4)
        val cp3 = new Constraint("3", 3, 5)

        val nId1 = NodeId("0.0.0.1", 1)
        val nId2 = NodeId("0.0.0.1", 2)
        val nId3 = NodeId("0.0.0.1", 3)
        val nId4 = NodeId("0.0.0.2", 5)
        val nId5 = NodeId("0.0.0.2", 1)
        val nId6 = NodeId("0.0.0.3", 4)
        val nId7 = NodeId("0.0.0.4", 5)
        val nId8 = NodeId("0.0.0.4", 6)

        val n1 = new NodeReport(nId1, "R1", null, new Resource(2, 1000), List(co7), null, DateTime.now, NodeState("NEW"), null)
        val n2 = new NodeReport(nId2, "R1", null, new Resource(2, 1000), List(co1, co8), null, DateTime.now, NodeState("NEW"), null)
        val n3 = new NodeReport(nId3, "R1", null, new Resource(2, 1000), List(co2), null, DateTime.now, NodeState("NEW"), null)
        val n4 = new NodeReport(nId4, "R2", null, new Resource(2, 1000), List(co2, co4, co8), null, DateTime.now, NodeState("NEW"), null)
        val n5 = new NodeReport(nId5, "R2", null, new Resource(2, 1000), List(co3, co8), null, DateTime.now, NodeState("NEW"), null)
        val n6 = new NodeReport(nId6, "R3", null, new Resource(2, 1000), List(co4, co2), null, DateTime.now, NodeState("NEW"), null)
        val n7 = new NodeReport(nId7, "R4", null, new Resource(4, 2000), List(co5, co1, co3, co7), null, DateTime.now, NodeState("NEW"), null)
        val n8 = new NodeReport(nId8, "R4", null, new Resource(8, 8000), List(co6, co2, co3, co7), null, DateTime.now, NodeState("NEW"), null)

        val cs = new ClusterStructure()
        cs.updateClusterStructure(4, null, List(n1, n2, n3, n4, n5, n6, n7, n8), null)
        var result = cs.getCurrentSamplingInformation(List(cp1, cp2, cp3))
        result.samplingRate should be(4)
        result.clusterNodes.length should be(8)
        result.clusterNodes(0) should be((nId1, null))
        result.clusterNodes(1) should be((nId2, null))
        result.clusterNodes(2) should be((nId3, List(co2)))
        result.clusterNodes(3) should be((nId4, List(co2, co4)))
        result.clusterNodes(6) should be((nId7, List(co3)))
        result.clusterNodes(7) should be((nId8, List(co6, co2, co3)))
        
        
        cs.updateClusterStructure(-1, null, null, List((true,co7)))
        result = cs.getCurrentSamplingInformation(List(cp1, cp2, cp3))
        result.samplingRate should be(4)
        result.clusterNodes.length should be(7)        
        result.clusterNodes(0) should be((nId2, null))
        result.clusterNodes(1) should be((nId3, List(co2)))
        result.clusterNodes(2) should be((nId4, List(co2, co4)))
        result.clusterNodes(5) should be((nId7, List(co3)))
        result.clusterNodes(6) should be((nId8, List(co6, co2, co3)))
        
        cs.updateClusterStructure(-1, null, null, List((false,co7),(true,co8)))
        result = cs.getCurrentSamplingInformation(List(cp1, cp2, cp3))
        result.samplingRate should be(4)
        result.clusterNodes.length should be(7)  
        cs.rareResource.length should be (1)
        cs.rareResource(0).name should be ("5")
        cs.rareResource(0).value should be (1)
        result.clusterNodes should contain((nId1, null))        
        result.clusterNodes should contain((nId3, List(co2)))
        result.clusterNodes should contain((nId4, List(co2, co4)))
        result.clusterNodes should contain((nId7, List(co3)))
        result.clusterNodes should contain((nId8, List(co6, co2, co3)))
        result.clusterNodes should not contain((nId2, null))

        cs.updateClusterStructure(-1, null, null, List((true,co7)))
        result = cs.getCurrentSamplingInformation(List(cp1, cp2, cp3))
        result.clusterNodes.length should be(6)  
        cs.rareResource.length should be (2)
        result.clusterNodes should contain((nId3, List(co2)))
        result.clusterNodes should contain((nId4, List(co2, co4)))
        result.clusterNodes should contain((nId7, List(co3)))
        result.clusterNodes should contain((nId8, List(co6, co2, co3)))
        result.clusterNodes should not contain((nId2, null))
        result.clusterNodes should not contain((nId1, null))
    }

}