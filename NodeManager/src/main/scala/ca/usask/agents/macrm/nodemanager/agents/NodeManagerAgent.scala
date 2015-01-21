package ca.usask.agents.macrm.nodemanager.agents

import ca.usask.agents.macrm.common.agents._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.nodemanager.utils._
import scala.util.control.Exception
import org.joda.time.DateTime
import akka.actor._

class NodeManagerAgent extends Agent {

    val resourceTracker = context.actorSelection(NodeManagerConfig.getResourceTrackerAddress())
    val nodeMonitor = context.actorOf(Props(new NodeMonitorAgent(self)), name = "nodeMonitorAgnet")
    val serverState = ServerState.apply()

    def receive = {
        case "initiateEvent"     => Event_initiate()
        case message: _HeartBeat => Handle_HeartBeat(message)
        case _                   => Handle_UnknownMessage
    }

    def Event_initiate() = {
        Logger.Log("NodeManagerAgent Initialization Start")

        if (!serverState.initializeServer())
            throw new Exception("Couldnot Initialize Server")
        //TODO: Should be blocking messaging         
        nodeMonitor ! "initiateEvent"

        Logger.Log("NodeManagerAgent Initialization End")
    }

    def Handle_HeartBeat(message: _HeartBeat) = resourceTracker ! new _HeartBeat(self, DateTime.now(), message._report)

}
