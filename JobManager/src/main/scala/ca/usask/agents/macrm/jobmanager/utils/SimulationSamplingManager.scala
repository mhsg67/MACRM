package ca.usask.agents.macrm.jobmanager.utils

import ca.usask.agents.macrm.common.records._
import scala.collection.mutable._
import akka.actor._
import scala.util.Random

class SimulationSamplingManager {

    var lastWave = 1
    var samplingRate = 2.0
    var clusterNodes: List[NodeId] = null
    var unscheduledTasks = List[(Int,TaskDescription)]()
    var scheduledTasks = List[TaskDescription]()

    def loadNewSamplingRate(newSamplingRate: Int) = samplingRate = newSamplingRate

    def loadSamplingInformation(samplingInformation: SamplingInformation) {
        samplingRate = samplingInformation.samplingRate
        clusterNodes = samplingInformation.clusterNodeWithoutConstraint
    }

    def addNewSubmittedTasks(tasks: List[TaskDescription]) = {
        unscheduledTasks = unscheduledTasks ++ tasks.map(x => (lastWave,x))
        lastWave = lastWave + 1
    }

    def getUnscheduledTaskOfWave(wave:Int):List[TaskDescription] = unscheduledTasks.filter(x => x._1 == wave).map(x => x._2)
    
    def getUnscheduledTasks(): List[TaskDescription] = unscheduledTasks.map(x => x._2)

    def getSamplingNode(tasks: List[TaskDescription], retry: Int): List[(NodeId, Resource)] = {
        val samplingCount = if (retry >= 1) samplingRate * math.pow(2.0, retry).toInt * tasks.length else samplingRate * tasks.length

        val minMemory = tasks.min(Ordering.by((x: TaskDescription) => x.resource.memory)).resource.memory
        val minVCore = tasks.min(Ordering.by((x: TaskDescription) => x.resource.virtualCore)).resource.virtualCore
        val minResource = new Resource(minMemory, minVCore)
        
        val randomNodes = Random.shuffle(clusterNodes).take(samplingCount.toInt)
        randomNodes.map(x => (x, minResource))
    }

    def whichTaskShouldSubmittedToThisNode(resource: Resource): Option[List[TaskDescription]] = {
        unscheduledTasks match {
            case List() => None
            case _ => {
                getMatchingUnscheduledTasksAndRemoveThem(resource) match {
                    case List() => None
                    case x      => Some(x)
                }
            }
        }
    }

    def getMatchingUnscheduledTasksAndRemoveThem(res: Resource): List[TaskDescription] = {
        val matchedTasks = getMatchingUnscheduledTasks(res, unscheduledTasks.map(x => x._2))
        matchedTasks.foreach(x => removeTheTaskFromUnscheduledTask(x))
        matchedTasks
    }

    def removeTheTaskFromUnscheduledTask(task: TaskDescription) = {
        unscheduledTasks = unscheduledTasks.filterNot(x => x._2.index == task.index)
        scheduledTasks = task :: scheduledTasks
    }

    def getMatchingUnscheduledTasks(res: Resource, tasks: List[TaskDescription]): List[TaskDescription] = {
        if (!res.isNotUsable()) {
            tasks match {
                case List() => Nil
                case x :: xs => {
                    if (x.resource.memory <= res.memory && x.resource.virtualCore <= res.virtualCore) {
                        val remainResource = new Resource(res.memory - x.resource.memory, res.virtualCore - x.resource.virtualCore)
                        x :: getMatchingUnscheduledTasks(remainResource, xs)
                    } else {
                        getMatchingUnscheduledTasks(res, xs)
                    }
                }
            }
        } else {
            Nil
        }
    }
}