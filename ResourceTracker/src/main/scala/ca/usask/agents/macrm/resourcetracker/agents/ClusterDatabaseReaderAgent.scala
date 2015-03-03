package ca.usask.agents.macrm.resourcetracker.agents

import ca.usask.agents.macrm.resourcetracker.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import org.joda.time._
import akka.actor._
import java.util.Formatter.DateTime

class ClusterDatabaseReaderAgent(val resourceTrackerAgent: ActorRef) extends Agent {

    import context.dispatcher
    override def preStart() = {
        context.system.scheduler.scheduleOnce(ResourceTrakerConfig.firstClusterStateUpdateDelay, self, "sendFirstClusterStateUpdate")
    }

    def receive = {
        case "initiateEvent"               => Event_initiate()
        case "sendFirstClusterStateUpdate" => Event_sendFirstClusterStateUpdate()
        case message: _JMHeartBeat         => Handle_JMHeartBeat(message)
        case _                             => Handle_UnknownMessage
    }

    def Event_sendFirstClusterStateUpdate() = {
        resourceTrackerAgent ! new _ClusterState(resourceTrackerAgent, DateTime.now(), 2, null, ClusterDatabase.getNodeIdToContaintsMaping(), null)
    }

    //TODO:Implement adaptive sampling here
    def Handle_JMHeartBeat(message: _JMHeartBeat) = {

    }

    def Event_initiate() = {
        Logger.Log("ClusterDatabaseReaderAgent Initialization")
    }

}