package ca.usask.agents.macrm.nodemanager.agents

import ca.usask.agents.macrm.nodemanager.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import org.joda.time.DateTime
import akka.actor._
import scala.concurrent.duration._
import scala.util._

class NodeMonitorAgent(val nodeManager: ActorRef, val serverState: ActorRef) extends Agent {

    val random = new Random(1000)
    val heartBeatInterval = new FiniteDuration(NodeManagerConfig.heartBeatStartDelay, MILLISECONDS)

    def Event_initiate() =
        Logger.Log("NodeMonitorAgent Initialization")

    import context.dispatcher
    override def preStart() = {
        val startDelay = new FiniteDuration(NodeManagerConfig.heartBeatStartDelay + random.nextInt(NodeManagerConfig.heartBeatInterval), MILLISECONDS)
        context.system.scheduler.scheduleOnce(startDelay, self, "heartBeatEvent")
    }

    def receive = {
        case "initiateEvent"     => Event_initiate()
        case "heartBeatEvent"    => serverState ! "heartBeatEvent"
        case message: _HeartBeat => Handle_heartBeat(message)
        case message             => Handle_UnknownMessage("NodeMonitorAgent", message)
    }

    def Handle_heartBeat(_message: _HeartBeat) = {
        nodeManager ! _message
        context.system.scheduler.scheduleOnce(heartBeatInterval, self, "heartBeatEvent")
    }
}