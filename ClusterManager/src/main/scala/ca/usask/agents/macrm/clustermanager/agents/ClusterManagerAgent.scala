package ca.usask.agents.macrm.clustermanager.agents

import ca.usask.agents.macrm.clustermanager.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime
import scala.collection.immutable._
import akka.actor._
import ca.usask.agents.macrm.common.agents._TaskSubmission

class ClusterManagerAgent extends Agent {

    var hasReceivedFirstClusterState = false
    var isInCentralizeState = 0

    val queueAgent = context.actorOf(Props[QueueAgent], name = "QueueAgent")
    val rackAgent = context.actorOf(Props(new RackAgent(0)), name = "RackAgent")
    val schedulerAgent1 = context.actorOf(Props(new SchedulerAgent(0, queueAgent, rackAgent)), name = "SchedulerAgent0")
    val schedulerAgent2 = context.actorOf(Props(new SchedulerAgent(1, queueAgent, rackAgent)), name = "SchedulerAgent1")
    val userInterfaceAgent = context.actorOf(Props(new UserInterfaceAgent(queueAgent)), name = "UserInterfaceAgent")
    val resourceTracker = context.actorSelection(ClusterManagerConfig.getResourceTrackerAddress())
    val nodeToRackMap = Map[(String, Int), ActorRef]()

    def receive = {
        case "initiateEvent"                    => Event_initiate()
        case "changeToCentralizedMode1"         => Handle_ChangeToCentralizedMode(1)
        case "changeToCentralizedMode2"         => Handle_ChangeToCentralizedMode(2)
        case message: _ClusterState             => Handle_ClusterState(message)
        case message: _TaskSubmissionFromJM     => Handle_TaskSubmissionFromJM(sender(), message)
        case message: _JobFinished              => Handle_JobFinished(message)
        case message: _ServerWithEmptyResources => Handle_ServerWithEmptyResources(message)
        case message: _EachUserShareOfCluster   => Handle_EachUserShareOfCluster(message)
        case message                            => Handle_UnknownMessage("ClusterManagerAgent", message)
    }

    def Handle_ServerWithEmptyResources(message: _ServerWithEmptyResources) =
        if (isInCentralizeState > 0) rackAgent ! message else queueAgent ! message

    def Handle_EachUserShareOfCluster(message: _EachUserShareOfCluster) =
        queueAgent ! message

    def Event_initiate() = {
        Logger.Log("ClusterManagerAgent Initialization")
        queueAgent ! "initiateEvent"
        userInterfaceAgent ! "initiateEvent"
        Logger.Log("ClusterManagerAgent Initialization End")
    }

    def Handle_JobFinished(message: _JobFinished) =
        userInterfaceAgent ! message

    def Handle_ChangeToCentralizedMode(mode: Int) = {
        if (isInCentralizeState == 0) {
            isInCentralizeState = mode
            sendToSchedulerAgents("initiateEvent" + mode.toString())
            rackAgent ! "initiateEvent"
        }
        else if (isInCentralizeState != mode) {
            isInCentralizeState = mode
            sendToSchedulerAgents("changeToCentralizedMode" + mode.toString())
        }
    }

    def sendToSchedulerAgents(message: String) = {
        schedulerAgent1 ! message
        schedulerAgent2 ! message
    }

    def Handle_TaskSubmissionFromJM(sender: ActorRef, message: _TaskSubmissionFromJM) = {
        val tasks = message._taskDescriptions.map(x => new TaskDescription(sender, x.jobId, x.index, x.duration, x.resource, x.relativeSubmissionTime, x.constraints, x.userId))
        queueAgent ! new _TaskSubmission(tasks)
    }

    def Handle_ClusterState(message: _ClusterState) = {
        isInCentralizeState = if (message._switchToDistributedMode == true) 0 else isInCentralizeState
        queueAgent ! message
    }
}