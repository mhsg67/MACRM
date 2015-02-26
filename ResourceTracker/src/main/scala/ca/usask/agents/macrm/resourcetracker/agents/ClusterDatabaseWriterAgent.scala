package ca.usask.agents.macrm.resourcetracker.agents

import ca.usask.agents.macrm.resourcetracker.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import org.joda.time._
import akka.actor._

class ClusterDatabaseWriterAgent(val resourceTrackerAgent: ActorRef) extends Agent {

    def receive = {
        case "initiateEvent"     => Event_initiate()
        case message: _HeartBeat => Handle_HeartBeat(message)
        case _                   => Handle_UnknownMessage
    }

    def Event_initiate() = {
        Logger.Log("ClusterDatabaseWriterAgent Initialization")
    }

    def Handle_HeartBeat(message: _HeartBeat) = {
        BasicClusterState.UpdateClusterState(message._source, message._time, message._report)
    }

}