package ca.usask.agents.macrm.clustermanager.agents

import ca.usask.agents.macrm.clustermanager.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime
import scala.collection.immutable._
import akka.actor._

class ClusterManagerAgent extends Agent {

    val queueAgent = context.actorOf(Props[QueueAgent], name = "QueueAgent")
    val userInterfaceAgent = context.actorOf(Props(new UserInterfaceAgent(queueAgent)), name = "UserInterfaceAgent")
    val resourceTracker = context.actorSelection(ClusterManagerConfig.getResourceTrackerAddress())

    var nodeToRackMap = Map[(String, Int), ActorRef]()
    var rackAgentList = List[ActorRef]()
    var schedulerAgentList = List[ActorRef]()

    def receive = {
        case "initiateEvent"                    => Event_initiate()
        case "changeToCentralizedMode"          => Handle_ChangeToCentralizedMode()
        case "changeToDistributedMode"          => Handle_ChangeToDistributedMode()
        case "finishedCentralizeScheduling"       => Handle_FinishedCentralizeScheduling(sender)
        case message: _ServerWithEmptyResources => Handle_ServerWithEmptyResources(message)
        case message: _EachUserShareOfCluster   => Handle_EachUserShareOfCluster(message)
        case message: _ServerStatusUpdate       => Handle_ServerStatusUpdate(message)
        case _                                  => Handle_UnknownMessage
        //TODO:Implement it //case message: _TaskSubmission
    }

    def Event_initiate() = {
        Logger.Log("ClusterManagerAgent Initialization")

        //TODO: Should be blocking messaging
        queueAgent ! "initiateEvent"
        userInterfaceAgent ! "initiateEvent"

        Logger.Log("ClusterManagerAgent Initialization End")
    }

    def Handle_ServerStatusUpdate(message: _ServerStatusUpdate) = {

    }

    def Handle_ChangeToCentralizedMode() = {
        queueAgent ! "changeToCentralizedMode"

    }

    def Handle_ChangeToDistributedMode() = {
        queueAgent ! "changeToDistributedMode"
        schedulerAgentList.foreach(x => x ! "changeToDistributedMode")
    }

    def Handle_ServerWithEmptyResources(message: _ServerWithEmptyResources) = queueAgent ! message

    def Handle_EachUserShareOfCluster(message: _EachUserShareOfCluster) = queueAgent ! message

    def Handle_FinishedCentralizeScheduling(sender: ActorRef) = {
        schedulerAgentList = schedulerAgentList.filter(x => x == sender)
        if(schedulerAgentList.isEmpty){        
            rackAgentList.foreach(x => {context.stop(x); x ! Kill})
            resourceTracker ! "finishedCentralizeScheduling"
        }
    }
}