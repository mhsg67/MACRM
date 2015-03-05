package ca.usask.agents.macrm.jobmanager.utils

import ca.usask.agents.macrm.common.records._
import scala.collection.mutable._
import akka.actor._
import scala.util.Random

object SamplingManager {

    var samplingRate = 2
    var clusterNodesWithConstraint: List[(NodeId, List[Constraint])] = null
    var clusterNodesWithoutConstraint: List[NodeId] = null
    var waveToTasks = Map[Int, List[(Boolean, TaskDescription)]]() //The Int is waveNumber

    def loadSamplingInformation(samplingInformation: SamplingInformation) {
        samplingRate = samplingInformation.samplingRate
        clusterNodesWithConstraint = samplingInformation.clusterNodesWithConstraint
        clusterNodesWithoutConstraint = samplingInformation.clusterNodeWithoutConstraint
    }

    def loadNewSamplingRate(newRate: Int) = samplingRate = newRate

    def findSamplingListForTasksWithConstraint(SRFTT: Int, tasks: List[TaskDescription]): (Int, List[(NodeId, Resource)]) = {
        val taskWithTheirProperNodes = tasks.map(x => (x.resource, findNodesWithProperCapabilities(SRFTT, x.constraints))).map(y => y._2.map(z => (z, y._1))).flatten
        (taskWithTheirProperNodes.length, taskWithTheirProperNodes)
    }

    def getSamplingNode(tasks: List[TaskDescription], retry: Int): List[(NodeId, Resource)] = {
        val samplingRateForThisTry = if (retry > 0) samplingRate * math.pow(2, retry) else samplingRate
        val (tasksWithConstraint, tasksWithoutConstraint) = tasks.partition(x => x.constraints != null || x.constraints != List())
        val (currentSamplingCount, resultPart1) = findSamplingListForTasksWithConstraint(samplingRateForThisTry.toInt, tasks)
        val remainingSamplingCount = (samplingRateForThisTry.toInt * tasks.length) - currentSamplingCount
        val minResource = new Resource(tasksWithoutConstraint.min(Ordering.by((x: TaskDescription) => x.resource.memory)).resource.memory,
            tasksWithoutConstraint.min(Ordering.by((x: TaskDescription) => x.resource.virtualCore)).resource.virtualCore)
        val resultPart2 = Random.shuffle(clusterNodesWithoutConstraint).take(remainingSamplingCount).map(x => (x, minResource))

        val result = resultPart1 ++ resultPart2
        result
    }

    def findNodesWithProperCapabilities(count: Int, constrints: List[Constraint]): List[NodeId] = {
        val tempListOfNodeId = clusterNodesWithConstraint.filter(x => doesAllCapablitiesMatchAllConstraints(x._2, constrints))
        if (tempListOfNodeId.length > count)
            Random.shuffle(tempListOfNodeId).take(count).map(x => x._1)
        else
            tempListOfNodeId.map(x => x._1)
    }

    def doesAllCapablitiesMatchAllConstraints(cap: List[Constraint], con: List[Constraint]): Boolean = {
        ???
    }

    def canFindProperResourcesForTheseConstraints(constraints: List[Constraint]): Boolean = true

    def whichTaskShouldSubmittedToThisNode(waveNumber: Int, resource: Resource, nodeId: NodeId): Option[List[TaskDescription]] = None

    def getUnscheduledTaskOfWave(waveNumber: Int) = waveToTasks.get(waveNumber).get.filter(x => x._1 == false).map(y => y._2)

    def addNewSubmittedTasksIntoWaveToTaks(waveNumber: Int, tasks: List[TaskDescription]) = {
        waveToTasks.update(waveNumber, tasks.map(x => (false, x)))
    }
}