package ca.usask.agents.macrm.clustermanager.agents

import ca.usask.agents.macrm.clustermanager.utils._
import ca.usask.agents.macrm.common.agents._
import com.typesafe.config.ConfigFactory
import akka.actor._

/**
 * TODO: For adaption part which switch to central decision making
 * create some scheduler actors and forward resource request for allocation to them
 */
class QueueAgent extends Agent {

    val system = ActorSystem.create("SchedulerAgent", ConfigFactory.load().getConfig("SchedulerAgent"))

    def receive = {
        case "initiateEvent"                    => Event_initiate()
        case message: _ServerWithEmptyResources => Handle_ServerWithEmptyResources(message)
        case message: _EachUserShareOfCluster   => Handle_EachUserShareOfCluster(message)
        case _                                  => Handle_UnknownMessage
    }

    def Event_initiate() = {
        Logger.Log("ResourceTrackerAgent Initialization")        
    }

    def Handle_ServerWithEmptyResources(message: _ServerWithEmptyResources) {

    }

    def Handle_EachUserShareOfCluster(message: _EachUserShareOfCluster) {

    }
}
