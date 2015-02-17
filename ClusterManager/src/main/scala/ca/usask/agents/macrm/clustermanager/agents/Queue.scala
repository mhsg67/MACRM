package ca.usask.agents.macrm.clustermanager.agents

import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import scala.collection.mutable._
import java.util.TaskQueue
import akka.actor._

trait AbstractQueue {
    def EnqueueJob(e: JobDescription): Unit
    def EnqueueTask(a: ActorRef, e: TaskDescription)
    
    def RemoveJob(e: JobDescription) 
    def RemoveTask(e: TaskDescription)

    def getFirstOrBestMatchJob(_resource: Resource, _capability: List[Int]): Option[JobDescription]
    def getFirtOrBestMatchTask(_resource: Resource, _capability: List[Int]): Option[Tuple2[ActorRef, TaskDescription]]
}

object AbstractQueue {
    def apply(queuetype: String): AbstractQueue = queuetype match {
        case "FIFOQueue" => new FIFOQueue()
    }
}

class FIFOQueue extends AbstractQueue {

    var JobQueue = new collection.mutable.MutableList[JobDescription]()

    var TaskQueue = new collection.mutable.MutableList[Tuple2[ActorRef, TaskDescription]]()

    def EnqueueJob(e: JobDescription) = JobQueue += e

    def EnqueueTask(a: ActorRef, e: TaskDescription) = TaskQueue += Tuple2(a, e)

    def RemoveJob(e: JobDescription) = JobQueue = JobQueue.filter(x => x.jobId != e.jobId)

    def RemoveTask(e: TaskDescription) = TaskQueue = TaskQueue.filter(x => (x._2.jobId != e.jobId && x._2.index != e.index))

    def getFirstOrBestMatchJob(_resource: Resource, _capability: List[Int]): Option[JobDescription] = JobQueue match {
        case MutableList() => None
        case _             => JobQueue.filter(x => doesJobDescriptionMatch(_resource, _capability, x)).headOption
    }

    def getFirtOrBestMatchTask(_resource: Resource, _capability: List[Int]): Option[Tuple2[ActorRef, TaskDescription]] = TaskQueue match {
        case MutableList() => None
        case _             => TaskQueue.filter(x => doesTaskDescriptionMatch(_resource, _capability, x._2)).headOption
    }

    private def doesJobDescriptionMatch(_resource: Resource, _capability: List[Int], _jobDescription: JobDescription) = {
        if (_jobDescription.numberOfTasks != 1)
            true
        else if (_jobDescription.constraints == null && (_jobDescription.tasks(0).resource < _resource || _jobDescription.tasks(0).resource == _resource))
            true
        else if (_jobDescription.constraints.foldLeft(true)((x, y) => _capability.contains(y) && x) && (_jobDescription.tasks(0).resource < _resource || _jobDescription.tasks(0).resource == _resource))
            true
        else
            false
    }

    private def doesTaskDescriptionMatch(_resource: Resource, _capability: List[Int], _taskDescription: TaskDescription) = {
        if (_taskDescription.constraints == null && _taskDescription.resource < _resource)
            true
        else if (_taskDescription.constraints.foldLeft(true)((x, y) => _capability.contains(y) && x) && _taskDescription.resource < _resource)
            true
        else
            false
    }
}

