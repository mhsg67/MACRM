package ca.usask.agents.macrm.clustermanager.agents

import ca.usask.agents.macrm.clustermanager.utils._
import ca.usask.agents.macrm.common.agents._
import akka.actor._

class SchedulerAgent(val queueAgent: ActorRef, val myId: Int, var RackAgents: List[ActorRef]) extends Agent {

    def receive = {
        case "initiateEvent"         => Event_initiate()
        case message: _JobSubmission => Handle_JobSubmission(message)
        case _                       => Handle_UnknownMessage
        //TODO:Implement it //case message: _TaskSubmission           => Hande_TaskSubmission(message)
    }

    def Event_initiate() = {
        Logger.Log(("SchedulerAgent" + myId.toString() + " Initialization"))

        queueAgent ! "getNextTaskForScheduling"
    }

    def Handle_JobSubmission(message: _JobSubmission) = {
        
    }
}
