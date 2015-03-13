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
    var completedWave = 0

    def loadSamplingInformation(samplingInformation: SamplingInformation) {
        samplingRate = samplingInformation.samplingRate
        clusterNodesWithConstraint = samplingInformation.clusterNodesWithConstraint
        clusterNodesWithoutConstraint = samplingInformation.clusterNodeWithoutConstraint
    }

    def getSamplingNode(tasks: List[TaskDescription], retry: Int): List[(NodeId, Resource)] = {
        val samplingRateForThisTry = if (retry > 0) samplingRate * math.pow(2, retry) else samplingRate
        val samplingCount = (samplingRateForThisTry.toInt * tasks.length)
        val minResource = new Resource(tasks.min(Ordering.by((x: TaskDescription) => x.resource.memory)).resource.memory,
            tasks.min(Ordering.by((x: TaskDescription) => x.resource.virtualCore)).resource.virtualCore)
        Random.shuffle(clusterNodesWithoutConstraint).take(samplingCount).map(x => (x, minResource))
    }

    def whichTaskShouldSubmittedToThisNode(lastWave: Int, resource: Resource, nodeId: NodeId): Option[List[TaskDescription]] = {
        val unscheduledTasks = getUnscheduledTasks(completedWave + 1, List())
        var result: Option[List[TaskDescription]] = None

        if (unscheduledTasks == List()) {
            completedWave = lastWave
            result = None
        }
        else {
            val temp = getBigestTasksThatCanFitThisResource(resource, unscheduledTasks.sortWith((x, y) => x.resource > y.resource), List())

            temp match {
                case List() => result = None
                case _ => {
                    removeFromUnscheduledTasks(completedWave + 1, temp)
                    result = Some(temp)
                }
            }
        }
        println("Result " + result.toString())
        result
    }

    @annotation.tailrec
    def getBigestTasksThatCanFitThisResource(resource: Resource, tasks: List[TaskDescription], result: List[TaskDescription]): List[TaskDescription] = tasks match {
        case List() => result
        case x :: xs => if (x.resource < resource)
            getBigestTasksThatCanFitThisResource(resource - x.resource, xs, x :: result)
        else
            getBigestTasksThatCanFitThisResource(resource, xs, result)
    }

    def getUnscheduledTasks(waveNumber: Int, tasks: List[TaskDescription]): List[TaskDescription] = waveToTasks.get(waveNumber) match {
        case None    => tasks
        case Some(x) => getUnscheduledTasks(waveNumber + 1, getUnscheduledTaskOfWave(waveNumber) ++ tasks)
    }

    def removeFromUnscheduledTasks(waveNumber: Int, tasks: List[TaskDescription]): Unit =
        if (!tasks.isEmpty) waveToTasks.get(waveNumber) match {
            case None    => Unit
            case Some(x) => removeFromUnscheduledTasks(waveNumber + 1, removeUnscheduledTasksOfWave(waveNumber, tasks))
        }

    def removeUnscheduledTasksOfWave(waveNumber: Int, toRemove: List[TaskDescription]): List[TaskDescription] = {
        var result = toRemove
        val scheduledTaskIndex = toRemove.map(x => x.index)
        var waveTasks = waveToTasks.get(waveNumber).get
        waveTasks.map { x =>
            if (scheduledTaskIndex.contains(x._2.index)) {
                (true, x._2)
                result = result.filterNot(y => y.index == x._2.index)
            }
            else (false, x._2)
        }
        waveToTasks.update(waveNumber, waveTasks)
        result
    }

    def getUnscheduledTaskOfWave(waveNumber: Int) = waveToTasks.get(waveNumber).get.filter(x => x._1 == false).map(y => y._2)

    def addNewSubmittedTasksIntoWaveToTaks(waveNumber: Int, tasks: List[TaskDescription]) = waveToTasks.update(waveNumber, tasks.map(x => (false, x)))

    def canFindProperResourcesForTheseConstraints(constraints: List[Constraint]): Boolean = true

    def loadNewSamplingRate(newRate: Int) = samplingRate = newRate

}
