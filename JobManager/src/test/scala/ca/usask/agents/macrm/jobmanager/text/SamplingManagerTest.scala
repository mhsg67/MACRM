package ca.usask.agents.macrm.jobmanager.text

import ca.usask.agents.macrm.jobmanager.utils._
import ca.usask.agents.macrm.common.records._
import org.joda.time._

class SamplingManagerTest extends UnitSpec {

    "loadSamplingInformation" should "update sampling Information with provided new information" in {
        val sampMang = new SamplingManager()

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

    "addNewSubmittedTasksIntoWaveToTasks" should "properly manage wave to tasks mapping" in {
        val sampMang = new SamplingManager()

        sampMang.waveToTasks.size should be(0)
        sampMang.completedWave should be(0)

        val ts1 = new TaskDescription(null, 1, 1, new Duration(10), new Resource(1000, 1), null, null, 1)
        val ts2 = new TaskDescription(null, 1, 2, new Duration(10), new Resource(500, 2), null, null, 1)
        val ts3 = new TaskDescription(null, 1, 3, new Duration(10), new Resource(500, 1), null, null, 1)
        val ts4 = new TaskDescription(null, 1, 4, new Duration(20), new Resource(1000, 2), null, null, 1)
        val ts5 = new TaskDescription(null, 1, 5, new Duration(20), new Resource(1000, 3), null, null, 1)
        val ts6 = new TaskDescription(null, 1, 6, new Duration(20), new Resource(250, 1), null, null, 1)
        val ts7 = new TaskDescription(null, 1, 7, new Duration(20), new Resource(250, 6), null, null, 1)
        val ts8 = new TaskDescription(null, 1, 8, new Duration(20), new Resource(250, 3), null, null, 1)

        sampMang.addNewSubmittedTasksIntoWaveToTaks(1, List(ts1, ts2, ts3))
        sampMang.waveToTasks.size should be(1)
        sampMang.completedWave should be(0)
        sampMang.waveToTasks.get(1).get.length should be(3)
        sampMang.waveToTasks.get(1).get should contain((false, ts1))
        sampMang.waveToTasks.get(1).get should contain((false, ts2))
        sampMang.waveToTasks.get(1).get should contain((false, ts3))

        sampMang.addNewSubmittedTasksIntoWaveToTaks(2, List(ts4, ts5, ts6, ts7, ts8))
        sampMang.waveToTasks.size should be(2)
        sampMang.waveToTasks.get(1).get.length should be(3)
        sampMang.waveToTasks.get(2).get.length should be(5)
        sampMang.waveToTasks.get(2).get should contain((false, ts4))
        sampMang.waveToTasks.get(2).get should contain((false, ts6))
        sampMang.waveToTasks.get(2).get should contain((false, ts8))

    }

    "getSamplingNode" should "return sound list of nodeId with minimum resource" in {
        val sampMang = new SamplingManager()

        val nId1 = new NodeId("127.0.0.1", 1, null)
        val nId2 = new NodeId("127.0.0.1", 2, null)
        val nId3 = new NodeId("127.0.0.2", 1, null)
        val nId4 = new NodeId("127.0.0.2", 3, null)
        val nId5 = new NodeId("127.0.0.3", 2, null)
        val nId6 = new NodeId("127.0.0.4", 4, null)
        val nId7 = new NodeId("127.0.0.3", 1, null)
        val nId8 = new NodeId("127.0.0.5", 10, null)
        val nId9 = new NodeId("127.0.0.3", 3, null)
        val nId10 = new NodeId("127.0.0.4", 1, null)

        val ts1 = new TaskDescription(null, 1, 1, new Duration(10), new Resource(1000, 1), null, null, 1)
        val ts2 = new TaskDescription(null, 1, 2, new Duration(10), new Resource(500, 2), null, null, 1)
        val ts3 = new TaskDescription(null, 1, 3, new Duration(10), new Resource(500, 1), null, null, 1)
        val ts4 = new TaskDescription(null, 1, 4, new Duration(20), new Resource(1000, 2), null, null, 1)
        val ts5 = new TaskDescription(null, 1, 5, new Duration(20), new Resource(1000, 3), null, null, 1)
        val ts6 = new TaskDescription(null, 1, 6, new Duration(20), new Resource(250, 1), null, null, 1)
        val ts7 = new TaskDescription(null, 1, 7, new Duration(20), new Resource(250, 6), null, null, 1)
        val ts8 = new TaskDescription(null, 1, 8, new Duration(20), new Resource(250, 3), null, null, 1)
        val ts9 = new TaskDescription(null, 1, 9, new Duration(30), new Resource(2000, 2), null, null, 1)
        val ts10 = new TaskDescription(null, 1, 10, new Duration(30), new Resource(2000, 2), null, null, 1)
        val ts11 = new TaskDescription(null, 1, 11, new Duration(40), new Resource(100, 4), null, null, 1)

        val newSamplingRate = new SamplingInformation(2, null, List(nId1, nId2, nId3, nId4, nId5, nId6, nId7, nId8, nId9, nId10))
        sampMang.loadSamplingInformation(newSamplingRate)

        sampMang.addNewSubmittedTasksIntoWaveToTaks(1, List(ts1, ts2, ts3))
        sampMang.addNewSubmittedTasksIntoWaveToTaks(2, List(ts4, ts5, ts6, ts7, ts8))
        sampMang.addNewSubmittedTasksIntoWaveToTaks(3, List(ts9, ts10))
        sampMang.addNewSubmittedTasksIntoWaveToTaks(4, List(ts11))

        val sampNodes = sampMang.getSamplingNode(List(ts1, ts2, ts3), 0)
        sampMang.samplingRate should be(2)
        sampNodes.length should be(6)
        sampNodes(1)._2 should be(new Resource(500, 1))
        sampNodes(0)._2 should be(new Resource(500, 1))
        sampNodes(4)._2 should be(new Resource(500, 1))
        sampNodes(5)._2 should be(new Resource(500, 1))
    }

    "getBigestTaskThatCanFitThisResource" should "return correct list of TaskDescription" in {
        val sampMang = new SamplingManager()

        val ts1 = new TaskDescription(null, 1, 1, new Duration(100), new Resource(1000, 1), new Duration(10), null, 1)
        val ts2 = new TaskDescription(null, 1, 2, new Duration(100), new Resource(500, 2), new Duration(10), null, 1)
        val ts3 = new TaskDescription(null, 1, 3, new Duration(100), new Resource(500, 1), new Duration(10), null, 1)
        val ts4 = new TaskDescription(null, 1, 4, new Duration(100), new Resource(1000, 2), new Duration(20), null, 1)
        val ts5 = new TaskDescription(null, 1, 5, new Duration(100), new Resource(1000, 3), new Duration(20), null, 1)
        val ts6 = new TaskDescription(null, 1, 6, new Duration(100), new Resource(250, 1), new Duration(20), null, 1)
        val ts7 = new TaskDescription(null, 1, 7, new Duration(100), new Resource(250, 6), new Duration(20), null, 1)
        val ts8 = new TaskDescription(null, 1, 8, new Duration(100), new Resource(250, 3), new Duration(20), null, 1)
        val ts9 = new TaskDescription(null, 1, 9, new Duration(100), new Resource(2000, 2), new Duration(30), null, 1)
        val ts10 = new TaskDescription(null, 1, 10, new Duration(100), new Resource(2000, 2), new Duration(30), null, 1)
        val ts11 = new TaskDescription(null, 1, 11, new Duration(100), new Resource(100, 4), new Duration(40), null, 1)

        sampMang.addNewSubmittedTasksIntoWaveToTaks(1, List(ts1, ts2, ts3))
        sampMang.addNewSubmittedTasksIntoWaveToTaks(2, List(ts4, ts5, ts6, ts7, ts8))
        sampMang.addNewSubmittedTasksIntoWaveToTaks(3, List(ts9, ts10))
        sampMang.addNewSubmittedTasksIntoWaveToTaks(4, List(ts11))

        var unscheduledTasks = sampMang.getUnscheduledTasks(1, List())
        unscheduledTasks.length should be(11)
        unscheduledTasks should contain(ts1)
        unscheduledTasks should contain(ts5)
        unscheduledTasks should contain(ts11)

        unscheduledTasks = sampMang.getUnscheduledTasks(2, List())
        unscheduledTasks.length should be(8)
        unscheduledTasks should contain(ts4)
        unscheduledTasks should contain(ts7)
        unscheduledTasks should contain(ts11)

        unscheduledTasks = sampMang.getUnscheduledTasks(4, List())
        unscheduledTasks.length should be(1)
        unscheduledTasks should contain(ts11)

        unscheduledTasks = sampMang.getUnscheduledTasks(1, List())
        unscheduledTasks.length should be(11)

        var fittedTasks = sampMang.getBigestTasksThatCanFitThisResource(new Resource(50, 100), unscheduledTasks, List())
        fittedTasks.length should be(0)

        unscheduledTasks = sampMang.getUnscheduledTasks(1, List())
        unscheduledTasks.length should be(11)

        fittedTasks = sampMang.getBigestTasksThatCanFitThisResource(new Resource(351, 6), unscheduledTasks, List())
        fittedTasks.length should be(2)
        fittedTasks should contain(ts6)
        fittedTasks should contain(ts11)

        val unscheduledTasksWave1 = sampMang.getUnscheduledTaskOfWave(1)
        unscheduledTasksWave1.length should be(3)
        unscheduledTasksWave1 should contain(ts1)
        unscheduledTasksWave1 should contain(ts2)
        unscheduledTasksWave1 should contain(ts3)

        unscheduledTasks = sampMang.getUnscheduledTasks(1, List())
        unscheduledTasks(0) should be(ts1)
        unscheduledTasks(3) should be(ts4)
        unscheduledTasks.length should be(11)

        fittedTasks = sampMang.getBigestTasksThatCanFitThisResource(new Resource(3000, 6), unscheduledTasks, List())
        fittedTasks.length should be(4)
        fittedTasks should contain(ts1)
        fittedTasks should contain(ts2)
        fittedTasks should contain(ts3)
        fittedTasks should contain(ts4)

        fittedTasks = sampMang.getBigestTasksThatCanFitThisResource(new Resource(1000, 6), unscheduledTasks, List())
        fittedTasks.length should be(1)
        fittedTasks should contain(ts1)
    }

    "removeFromUnscheduledTasks" should "properly remove tasks from waveToTasks" in {
        val sampMang = new SamplingManager()

        val ts1 = new TaskDescription(null, 1, 1, new Duration(100), new Resource(1000, 1), new Duration(10), null, 1)
        val ts2 = new TaskDescription(null, 1, 2, new Duration(100), new Resource(500, 2), new Duration(10), null, 1)
        val ts3 = new TaskDescription(null, 1, 3, new Duration(100), new Resource(500, 1), new Duration(10), null, 1)
        val ts4 = new TaskDescription(null, 1, 4, new Duration(100), new Resource(1000, 2), new Duration(20), null, 1)
        val ts5 = new TaskDescription(null, 1, 5, new Duration(100), new Resource(1000, 3), new Duration(20), null, 1)
        val ts6 = new TaskDescription(null, 1, 6, new Duration(100), new Resource(250, 1), new Duration(20), null, 1)
        val ts7 = new TaskDescription(null, 1, 7, new Duration(100), new Resource(250, 6), new Duration(20), null, 1)
        val ts8 = new TaskDescription(null, 1, 8, new Duration(100), new Resource(250, 3), new Duration(20), null, 1)
        val ts9 = new TaskDescription(null, 1, 9, new Duration(100), new Resource(2000, 2), new Duration(30), null, 1)
        val ts10 = new TaskDescription(null, 1, 10, new Duration(100), new Resource(2000, 2), new Duration(30), null, 1)
        val ts11 = new TaskDescription(null, 1, 11, new Duration(100), new Resource(100, 4), new Duration(40), null, 1)

        sampMang.addNewSubmittedTasksIntoWaveToTaks(1, List(ts1, ts2, ts3))
        sampMang.addNewSubmittedTasksIntoWaveToTaks(2, List(ts4, ts5, ts6, ts7, ts8))
        sampMang.addNewSubmittedTasksIntoWaveToTaks(3, List(ts9, ts10))
        sampMang.addNewSubmittedTasksIntoWaveToTaks(4, List(ts11))

        var unscheduledTasks = sampMang.getUnscheduledTasks(1, List())
        var fittedTasks = sampMang.getBigestTasksThatCanFitThisResource(new Resource(3000, 6), unscheduledTasks, List())
        fittedTasks.length should be(4)
        fittedTasks should contain(ts1)
        fittedTasks should contain(ts2)
        fittedTasks should contain(ts3)
        fittedTasks should contain(ts4)


        
        sampMang.removeFromUnscheduledTasks(1, fittedTasks)
        unscheduledTasks = sampMang.getUnscheduledTasks(1, List())
        unscheduledTasks.length should be(7)
        unscheduledTasks should not contain (ts1)
        unscheduledTasks should not contain (ts2)
        unscheduledTasks should not contain (ts3)
        unscheduledTasks should not contain (ts4)

    }

}