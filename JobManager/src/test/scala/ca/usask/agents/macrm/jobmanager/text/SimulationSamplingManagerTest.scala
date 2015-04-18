package ca.usask.agents.macrm.jobmanager.text

import ca.usask.agents.macrm.jobmanager.utils._
import ca.usask.agents.macrm.common.records._
import org.joda.time._

class SimulationSamplingManagerTest extends UnitSpec {

    "loadSamplingInformation" should "update sampling Information with provided new information" in {
        val sampMang = new SimulationSamplingManager()

        sampMang.samplingRate should be(2)
        sampMang.clusterNodes should be(null)

        val nId1 = new NodeId("127.0.0.1", 1, null)
        val nId2 = new NodeId("127.0.0.1", 2, null)
        val nId3 = new NodeId("127.0.0.2", 1, null)
        val nId4 = new NodeId("127.0.0.2", 3, null)
        val nId5 = new NodeId("127.0.0.3", 2, null)
        val nId6 = new NodeId("127.0.0.4", 4, null)
        val nId7 = new NodeId("127.0.0.3", 1, null)

        val newSamplingRate = new SamplingInformation(4, null, List(nId1, nId2, nId3, nId4, nId5, nId6))
        sampMang.loadSamplingInformation(newSamplingRate)

        sampMang.samplingRate should be(4)
        sampMang.clusterNodes.length should be(6)
        sampMang.clusterNodes should contain(nId3)
        sampMang.clusterNodes should contain(nId1)
        sampMang.clusterNodes should contain(nId6)
    }
}