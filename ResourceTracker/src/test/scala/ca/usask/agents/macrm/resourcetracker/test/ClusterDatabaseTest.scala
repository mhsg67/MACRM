package ca.usask.agents.macrm.resourcetracker.test

import ca.usask.agents.macrm.resourcetracker.utils._
import ca.usask.agents.macrm.common.records._

class ClusterDatabaseTest extends UnitSpec {

    "updateNodeState" should "properly update nodeIdToNodeStateTable by new providided data" in {

        val nId1 = new NodeId("0.0.0.1", 1, null)
        ClusterDatabase.updateNodeState(nId1,
            new Resource(4000, 8), new Resource(2500, 5), null, new Utilization(), 10)

        ClusterDatabase.nodeIdToNodeStateTable.get(nId1).get shouldBe a[nodeIdToNodeStateRow]
        ClusterDatabase.nodeIdToNodeStateTable.get(nId1).get.usedResources should be(new Resource(2500, 5))
        ClusterDatabase.nodeIdToNodeStateTable.size should be(1)

        ClusterDatabase.updateNodeState(nId1,
            new Resource(4000, 8), new Resource(1000, 3), null, new Utilization(), 5)
        ClusterDatabase.nodeIdToNodeStateTable.get(nId1).get.usedResources should be(new Resource(1000, 3))
        ClusterDatabase.nodeIdToNodeStateTable.get(nId1).get.queueState should be(5)
        ClusterDatabase.nodeIdToNodeStateTable.size should be(1)

    }

    "updateNodeContainer" should "properly update both nodeIdToContainerTable and userIdToUserShareTable" in {
        val nId1 = new NodeId("0.0.0.1", 1, null)
        val cnt11 = new Container(0, 1, 1, 0, new Resource(500, 1))
        val cnt12 = new Container(2, 2, 2, 1, new Resource(1000, 1))
        val cnt13 = new Container(1, 3, 1, 12, new Resource(1000, 2))
        val cnt14 = new Container(3, 1, 1, 1, new Resource(250, 1))

        val nId2 = new NodeId("0.0.0.2", 1, null)
        val cnt21 = new Container(0, 2, 1, 1, new Resource(1000, 2))
        val cnt22 = new Container(1, 1, 1, 2, new Resource(2000, 2))
        val cnt23 = new Container(2, 4, 1, 0, new Resource(4000, 4))
        val cnt24 = new Container(3, 5, 1, 0, new Resource(200, 1))

        val cntN11 = new Container(0, 1, 1, 0, new Resource(755, 3))

        ClusterDatabase.updateNodeContainer(nId1, List(cnt11, cnt12, cnt13, cnt14))
        ClusterDatabase.updateNodeContainer(nId2, List(cnt21, cnt22, cnt23))
        ClusterDatabase.nodeIdToContainerTable.size should be(2)
        ClusterDatabase.nodeIdToContainerTable.get(nId1).get shouldBe a[List[_]]
        ClusterDatabase.nodeIdToContainerTable.get(nId2).get shouldBe a[List[_]]
        ClusterDatabase.nodeIdToContainerTable.get(nId1).get.length should be(4)
        ClusterDatabase.nodeIdToContainerTable.get(nId1).get(0) shouldBe a[Container]
        ClusterDatabase.nodeIdToContainerTable.get(nId1).get(0) should be(cnt11)
        ClusterDatabase.nodeIdToContainerTable.get(nId2).get.length should be(3)
        ClusterDatabase.nodeIdToContainerTable.get(nId2).get(0) shouldBe a[Container]
        ClusterDatabase.nodeIdToContainerTable.get(nId2).get(2) should be(cnt23)

        ClusterDatabase.userIdToUserShareTable.size should be(4)
        ClusterDatabase.userIdToUserShareTable.get(1).get shouldBe a[Resource]
        ClusterDatabase.userIdToUserShareTable.get(1).get should be(new Resource(2750, 4))
        ClusterDatabase.userIdToUserShareTable.get(2).get should be(new Resource(2000, 3))
        ClusterDatabase.userIdToUserShareTable.get(3).get should be(new Resource(1000, 2))
        ClusterDatabase.userIdToUserShareTable.get(4).get should be(new Resource(4000, 4))

        ClusterDatabase.updateNodeContainer(nId1, List(cntN11, cnt12))
        ClusterDatabase.updateNodeContainer(nId2, List(cnt22, cnt23,cnt24))
        ClusterDatabase.nodeIdToContainerTable.size should be(2)
        ClusterDatabase.nodeIdToContainerTable.get(nId1).get shouldBe a[List[_]]
        ClusterDatabase.nodeIdToContainerTable.get(nId2).get shouldBe a[List[_]]
        ClusterDatabase.nodeIdToContainerTable.get(nId1).get.length should be(2)
        ClusterDatabase.nodeIdToContainerTable.get(nId1).get(0) shouldBe a[Container]
        ClusterDatabase.nodeIdToContainerTable.get(nId1).get(0) should be(cntN11)
        ClusterDatabase.nodeIdToContainerTable.get(nId2).get.length should be(3)
        ClusterDatabase.nodeIdToContainerTable.get(nId2).get(0) shouldBe a[Container]
        ClusterDatabase.nodeIdToContainerTable.get(nId2).get(2) should be(cnt24)

        ClusterDatabase.userIdToUserShareTable.size should be(5)
        ClusterDatabase.userIdToUserShareTable.get(1).get shouldBe a[Resource]
        ClusterDatabase.userIdToUserShareTable.get(5).get should be (new Resource(200,1))
        ClusterDatabase.userIdToUserShareTable.get(1).get should be (new Resource(2755,5))
        ClusterDatabase.userIdToUserShareTable.get(2).get should be(new Resource(1000, 1))
        ClusterDatabase.userIdToUserShareTable.get(3).get should be(new Resource(0, 0))
        ClusterDatabase.userIdToUserShareTable.get(4).get should be(new Resource(4000, 4))
    }

}