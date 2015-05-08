package ca.usask.agents.macrm.nodemanager.agents

import ca.usask.agents.macrm.common.agents._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.nodemanager.utils._
import scala.util.control.Exception
import org.joda.time.DateTime
import akka.actor._

class NodeManagerAgent(val id: Int = 0) extends Agent {

    val resourceTracker = context.actorSelection(NodeManagerConfig.getResourceTrackerAddress)
    val serverState = context.actorOf(Props(new ServerStateAgent(self)), name = "ServerStateAgent" + id.toString())
    val nodeMonitor = context.actorOf(Props(new NodeMonitorAgent(self, serverState)), name = "nodeMonitorAgnet" + id.toString())
    val containerManager = context.actorOf(Props(new ContainerManagerAgent(self, serverState)), name = "ContainerManagerAgent" + id.toString())

    def receive = {
        case "ridi"                             => println("ridid")
        case "initiateEvent"                    => Event_initiate()
        case message: _HeartBeat                => resourceTracker ! new _HeartBeat(self, DateTime.now(), message._report)
        case message: _ResourceSamplingInquiry  => containerManager ! new _ResourceSamplingInquiry(sender(), message._time, message._minRequiredResource, message._jobId)
        case message: _ResourceSamplingCancel   => containerManager ! new _ResourceSamplingCancel(sender(), message._time, message._jobId)
        case message: _AllocateContainerFromCM  => containerManager ! message
        case message: _AllocateContainerFromJM  => containerManager ! message
        case message: _ResourceSamplingResponse => Handle_ResourceSamplingResponse(message)
        case message                            => Handle_UnknownMessage("NodeManagerAgent", message)
    }

    def Event_initiate() = {
        Logger.Log("NodeManagerAgent Initialization Start")
        serverState ! "initiateEvent"
        nodeMonitor ! "initiateEvent"
        containerManager ! "initiateEvent"
        Logger.Log("NodeManagerAgent Initialization End")
    }

    def Handle_ResourceSamplingResponse(message: _ResourceSamplingResponse) =
        message._source ! new _ResourceSamplingResponse(self, message._time, message._availableResource)
}
