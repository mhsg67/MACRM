package ca.usask.agents.macrm.resourcetracker.agents

import ca.usask.agents.macrm.resourcetracker.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import org.joda.time._
import akka.actor._

class ClusterDatabaseReaderAgent(val resourceTrackerAgent: ActorRef) extends Agent {

    import context.dispatcher
    override def preStart() = {
        context.system.scheduler.scheduleOnce(ResourceTrakerConfig.firstClusterStateUpdateDelay, self, "sendFirstClusterStateUpdate")
    }

    def receive = {
        case "initiateEvent"               => Event_initiate()
        case "sendFirstClusterStateUpdate" => Event_sendFirstClusterStateUpdate()
        case _                             => Handle_UnknownMessage
    }

    def Event_sendFirstClusterStateUpdate() = {

    }

    def Event_initiate() = {
        Logger.Log("ClusterDatabaseReaderAgent Initialization")
    }

}