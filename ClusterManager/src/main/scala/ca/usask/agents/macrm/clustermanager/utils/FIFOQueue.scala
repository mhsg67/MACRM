package ca.usask.agents.macrm.clustermanager.utils

import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import scala.collection.mutable._
import akka.actor._

class FIFOQueue extends AbstractQueue {
    var requestCounter: Long = 1
    var generalQueue = new MutableList[(Long, Any)]()

    def isEmpty: Boolean = generalQueue.length == 0

    def EnqueueRequest(e: Any) = {
        if (requestCounter == Long.MaxValue) requestCounter = 1 else requestCounter += 1
        generalQueue += ((requestCounter, e))
    }

    def DequeueRequest(): Either[JobDescription, TaskDescription] = {
        val headOfQueue = generalQueue.get(0)
        headOfQueue match {
            case None => Left(null)
            case Some(x) => {
                generalQueue = generalQueue.drop(1)
                x._2 match {
                    case job: JobDescription   => Left(job)
                    case task: TaskDescription => Right(task)
                }
            }
        }
    }

    def getBestMatches(resource: Resource, capability: List[Constraint]): Option[(List[JobDescription], List[TaskDescription])] = {
        if (resource.isNotUsable())
            None
        else {
            val preResult = iterateQueueToFindMatchRequests(resource, capability, generalQueue)
            if (preResult == List())
                None
            else
                Some(removeMatchedRequestAndSeparateThem(preResult))
        }
    }

    def removeMatchedRequestAndSeparateThem(matches: List[(Long, Any)]): (List[JobDescription], List[TaskDescription]) = {
        var matchedJobs = List[JobDescription]()
        var matchedTasks = List[TaskDescription]()

        matches.foreach(x => {
            RemoveRequest(x._1)
            x._2 match {
                case job: JobDescription   => matchedJobs = job :: matchedJobs
                case task: TaskDescription => matchedTasks = task :: matchedTasks
            }
        })

        var requestR = matchedJobs.foldLeft(new Resource(0, 0))((x, y) => x + y.tasks(0).resource)
        requestR = matchedTasks.foldLeft(requestR)((x, y) => x + y.resource)

        (matchedJobs, matchedTasks)
    }

    def iterateQueueToFindMatchRequests(res: Resource, cap: List[Constraint], que: MutableList[(Long, Any)]): List[(Long, Any)] = que match {
        case MutableList() => List()
        case _ => que.head._2 match {
            case job: JobDescription =>
                if (res.memory >= job.tasks(0).resource.memory &&
                    res.virtualCore >= job.tasks(0).resource.virtualCore &&
                    doesJobDescriptionMatch(res, cap, job)) {
                    val remainingResource = new Resource(res.memory - job.tasks(0).resource.memory, res.virtualCore - job.tasks(0).resource.virtualCore)
                    if (remainingResource.isNotUsable())
                        (que.head._1, que.head._2) :: Nil
                    else
                        (que.head._1, que.head._2) :: iterateQueueToFindMatchRequests(remainingResource, cap, que.tail)
                }
                else {
                    iterateQueueToFindMatchRequests(res, cap, que.tail)
                }
            case task: TaskDescription =>
                if (res.memory >= task.resource.memory &&
                    res.virtualCore >= task.resource.virtualCore &&
                    doesTaskDescriptionMatch(res, cap, task)) {
                    val remainingResource = new Resource(res.memory - task.resource.memory, res.virtualCore - task.resource.virtualCore)
                    if (remainingResource.isNotUsable())
                        (que.head._1, que.head._2) :: Nil
                    else
                        (que.head._1, que.head._2) :: iterateQueueToFindMatchRequests(remainingResource, cap, que.tail)
                }
                else {
                    iterateQueueToFindMatchRequests(res, cap, que.tail)
                }
        }
    }

    def doesJobDescriptionMatch(resource: Resource, capability: List[Constraint], jobDescription: JobDescription) = {
        if (jobDescription.numberOfTasks != 1)
            true
        else if (jobDescription.constraints.isEmpty &&
            jobDescription.tasks(0).resource.memory <= resource.memory &&
            jobDescription.tasks(0).resource.virtualCore <= resource.virtualCore)
            true
        else if (!jobDescription.constraints.isEmpty && capability.isEmpty)
            false
        else if (jobDescription.constraints.foldLeft(true)((x, y) => doesConstraintMatch(capability, y) && x) &&
            jobDescription.tasks(0).resource.memory <= resource.memory &&
            jobDescription.tasks(0).resource.virtualCore <= resource.virtualCore)
            true
        else
            false
    }

    def doesTaskDescriptionMatch(resource: Resource, capability: List[Constraint], taskDescription: TaskDescription) = {
        if (taskDescription.constraints.isEmpty &&
            taskDescription.resource.memory <= resource.memory &&
            taskDescription.resource.virtualCore <= resource.virtualCore)
            true
        else if (capability.isEmpty)
            false
        else if (taskDescription.constraints.foldLeft(true)((x, y) => doesConstraintMatch(capability, y) && x) &&
            taskDescription.resource.memory <= resource.memory &&
            taskDescription.resource.virtualCore <= resource.virtualCore)
            true
        else
            false
    }

    def doesConstraintMatch(resourceCapabilityList: List[Constraint], taskConstraint: Constraint) = resourceCapabilityList.find(x => x.name == taskConstraint.name) match {
        case None => false
        case Some(x) =>
            taskConstraint.operator match {
                case 0 => if (x.value == taskConstraint.value) true else false
                case 1 => if (x.value != taskConstraint.value) true else false
                case 2 => if (x.value < taskConstraint.value) true else false
                case 3 => if (x.value > taskConstraint.value) true else false
            }
    }

    def RemoveRequest(index: Long) = generalQueue = generalQueue.filter(x => (x._1 != index))
}