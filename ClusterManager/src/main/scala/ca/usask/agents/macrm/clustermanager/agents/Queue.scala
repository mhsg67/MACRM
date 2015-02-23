package ca.usask.agents.macrm.clustermanager.agents

import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import scala.collection.mutable._
import java.util.TaskQueue
import akka.actor._

trait AbstractQueue {
    def EnqueueJob(e: JobDescription): Unit
    def EnqueueTask(e: TaskDescription)

    def RemoveJob(e: JobDescription)
    def RemoveTask(e: TaskDescription)

    def getFirstOrBestMatchJob(resource: Resource, capability: List[Int]): Option[JobDescription]
    def getFirtOrBestMatchTask(resource: Resource, capability: List[Int]): Option[TaskDescription]

    def doesJobDescriptionMatch(resource: Resource, capability: List[Int], jobDescription: JobDescription): Boolean
    def doesTaskDescriptionMatch(resource: Resource, capability: List[Int], taskDescription: TaskDescription): Boolean
}

object AbstractQueue {
    def apply(queuetype: String): AbstractQueue = queuetype match {
        case "FIFOQueue" => new FIFOQueue()
    }
}

class FIFOQueue extends AbstractQueue {

    var JobQueue = new MutableList[JobDescription]()

    var TaskQueue = new MutableList[TaskDescription]()

    def EnqueueJob(e: JobDescription) = JobQueue += e

    def EnqueueTask(e: TaskDescription) = TaskQueue += e

    def RemoveJob(e: JobDescription) = JobQueue = JobQueue.filter(x => x.jobId != e.jobId)

    def RemoveTask(e: TaskDescription) = TaskQueue = TaskQueue.filter(x => (x.jobId != e.jobId && x.index != e.index))

    def getFirstOrBestMatchJob(resource: Resource, capability: List[Int]): Option[JobDescription] = JobQueue match {
        case MutableList() => None
        case _             => JobQueue.find(x => doesJobDescriptionMatch(resource, capability, x))
    }

    def getFirtOrBestMatchTask(resource: Resource, capability: List[Int]): Option[TaskDescription] = TaskQueue match {
        case MutableList() => None
        case _             => TaskQueue.find(x => doesTaskDescriptionMatch(resource, capability, x))
    }

    def doesJobDescriptionMatch(resource: Resource, capability: List[Int], jobDescription: JobDescription) = {
        if (jobDescription.numberOfTasks != 1)
            true
        else if (jobDescription.constraints == null && jobDescription.tasks(0).resource < resource)
            true
        else if (capability == null)
            false
        else if (jobDescription.constraints.foldLeft(true)((x, y) => capability.contains(y) && x) && jobDescription.tasks(0).resource < resource)
            true
        else
            false
    }

    def doesTaskDescriptionMatch(_resource: Resource, _capability: List[Int], _taskDescription: TaskDescription) = {
        if (_taskDescription.constraints == null && _taskDescription.resource < _resource)
            true
        else if (_capability == null)
            false
        else if (_taskDescription.constraints.foldLeft(true)((x, y) => _capability.contains(y) && x) && _taskDescription.resource < _resource)
            true
        else
            false
    }
}

