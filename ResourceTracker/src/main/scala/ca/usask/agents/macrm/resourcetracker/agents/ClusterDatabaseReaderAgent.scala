package ca.usask.agents.macrm.resourcetracker.agents

import ca.usask.agents.macrm.resourcetracker.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import org.joda.time._
import akka.actor._
import java.util.Formatter.DateTime

/*
 * TODO: this class should be completely implemented to provide information in response to user queries 
 */
class ClusterDatabaseReaderAgent(val resourceTrackerAgent: ActorRef) extends Agent {

    def receive = {
        case "initiateEvent"       => Event_initiate()
        case message: _JMHeartBeat => Handle_JMHeartBeat(message)
        case message               => Handle_UnknownMessage("ClusterDatabaseReaderAgent", message)
    }

    def Event_initiate() =
        Logger.Log("ClusterDatabaseReaderAgent Initialization")

    def Handle_JMHeartBeat(message: _JMHeartBeat) = {

    }
}