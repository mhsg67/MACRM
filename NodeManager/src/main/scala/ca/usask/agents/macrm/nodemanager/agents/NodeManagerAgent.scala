package ca.usask.agents.macrm.nodemanager.agents

import ca.usask.agents.macrm.common.agents._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.nodemanager.utils._
import scala.util.control.Exception
import org.joda.time.DateTime
import akka.actor._

class NodeManagerAgent extends Agent {

    val resourceTracker = context.actorSelection(NodeManagerConfig.getResourceTrackerAddress())
    val serverState = context.actorOf(Props(new ServerStateAgent(self)), name = "ServerStateAgent")
    val nodeMonitor = context.actorOf(Props(new NodeMonitorAgent(self, serverState)), name = "nodeMonitorAgnet")
    val containerManager = context.actorOf(Props(new ContainerManagerAgent(self, serverState)), name = "ContainerManagerAgent")

    def receive = {
        case "initiateEvent"                         => Event_initiate()
        case message: _NodeManagerSimulationInitiate => Event_NodeManagerSimulationInitiate(message)
        case message: _HeartBeat                     => resourceTracker ! new _HeartBeat(self, DateTime.now(), message._report)
        case message: _ResourceSamplingInquiry       => containerManager ! new _ResourceSamplingInquiry(sender(), message._time, message._minRequiredResource, message._jobId)
        case message: _ResourceSamplingCancel        => containerManager ! new _ResourceSamplingCancel(sender(), message._time, message._jobId)
        case message: _AllocateContainerFromCM       => containerManager ! message
        case message: _AllocateContainerFromJM       => containerManager ! message
        case _                                       => Handle_UnknownMessage("NodeManagerAgent")
    }

    def Event_initiate() = {
        Logger.Log("NodeManagerAgent Initialization Start")

        //TODO: Should be blocking messaging
        serverState ! "initiateEvent"
        nodeMonitor ! "initiateEvent"
        containerManager ! "initiateEvent"

        Logger.Log("NodeManagerAgent Initialization End")
    }

    def Event_NodeManagerSimulationInitiate(message: _NodeManagerSimulationInitiate) = {
        Logger.Log("NodeManagerAgent Initialization Start")

        serverState ! message
        nodeMonitor ! "initiateEvent"
        containerManager ! "simulationInitiateEvent"

        Logger.Log("NodeManagerAgent Initialization End")
    }
}
