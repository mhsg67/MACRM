package ca.usask.agents.macrm.clustermanager.agents

import ca.usask.agents.macrm.clustermanager.utils._
import ca.usask.agents.macrm.common.agents._
import ca.usask.agents.macrm.common.records._
import scala.concurrent.duration._
import akka.actor._
import org.joda.time.DateTime

class SchedulerAgent(val myId: Int, val queueAgent: ActorRef, val rackAgent: ActorRef) extends Agent {

    var jobToSchedule: JobDescription = null
    var taskToSchedule: TaskDescription = null
    var properNode: NodeId = null
    var isInCentralizeState = 1

    def receive = {
        case "initiateEvent1"                       => Event_initiate(1)
        case "initiateEvent2"                       => Event_initiate(2)
        case "changeToCentralizedMode1"             => Handle_changeToCentralizedMode(1)
        case "changeToCentralizedMode2"             => Handle_changeToCentralizedMode(2)
        case "trySchedulingAgain"                   => Event_trySchedulingAgain()
        case "transactionCompleted"                 => Handle_transactionCompleted()
        case "Scheduled"                            => Handle_Scheduled()
        case message: _ridi                         => Handle_ridi(message)
        case message: _NodesWithFreeResources       => Handle_NodesWithFreeResources(message)
        case message: _headOfSchedulingQueue        => Handle_headOfSchedulingQueue(message)
        case message: _UnsuccessfulPartOfTrasaction => Handle_UnsuccessfulPartOfTrasaction(message)
        case message                                => Handle_UnknownMessage("SchedulerAgent", message)
    }

    import context.dispatcher
    def Handle_changeToCentralizedMode(mode: Int) =
        isInCentralizeState = mode

    def Handle_ridi(message: _ridi) =
        startScheduling(null, null)

    def waitForConditionChange(time: Long) =
        context.system.scheduler.scheduleOnce(time.milliseconds, self, "trySchedulingAgain")

    def Handle_UnsuccessfulPartOfTrasaction(message: _UnsuccessfulPartOfTrasaction) = //TODO: for now since we just schedule single task its okay
        startScheduling(null, null)

    def Event_initiate(mode: Int) = {
        Logger.Log(("SchedulerAgent" + myId.toString() + " Initialization"))
        isInCentralizeState = mode
        queueAgent ! "getNextTaskForScheduling"
    }

    def Event_trySchedulingAgain() = {
        if (taskToSchedule != null || jobToSchedule != null)
            startScheduling(null, null)
        else
            queueAgent ! "getNextTaskForScheduling"
    }

    def Handle_headOfSchedulingQueue(message: _headOfSchedulingQueue) = {
        val schedulingRequest = message.jobOrTask
        schedulingRequest match {
            case Left(x)  => if (x == null) waitForConditionChange(5) else startScheduling(x, null)
            case Right(y) => startScheduling(null, y)
        }
    }

    def Handle_Scheduled() = {
        jobToSchedule = null
        taskToSchedule = null
        properNode = null

        queueAgent ! "getNextTaskForScheduling"
    }

    def startScheduling(job: JobDescription, task: TaskDescription) = {
        if (job != null) jobToSchedule = job
        if (task != null) taskToSchedule = task
        rackAgent ! "getNodeWithFreeResources"
    }

    def Handle_NodesWithFreeResources(message: _NodesWithFreeResources) = {
        val requiredResource = if (jobToSchedule == null) taskToSchedule.resource else jobToSchedule.tasks(0).resource
        properNode = findProperNode(message.nodes, requiredResource)

        if (properNode == null)
            waitForConditionChange(10)
        else
            rackAgent ! new _UpdateNodesWithFreeResourcesTransaction(List((properNode, requiredResource)))
    }

    def Handle_transactionCompleted() = {
        if (jobToSchedule != null) properNode.agent ! new _AllocateContainerFromSA(self, DateTime.now(), null, List((jobToSchedule, new SamplingInformation(0, null, null))), isInCentralizeState)
        if (taskToSchedule != null)
            properNode.agent ! new _AllocateContainerFromSA(self, DateTime.now(), List(taskToSchedule), null, isInCentralizeState)
    }

    def findProperNode(nodes: List[(NodeId, Resource)], res: Resource): NodeId = nodes match {
        case Nil     => null
        case x :: xs => if (x._2.memory >= res.memory && x._2.virtualCore >= res.virtualCore) x._1 else findProperNode(xs, res)
    }

}