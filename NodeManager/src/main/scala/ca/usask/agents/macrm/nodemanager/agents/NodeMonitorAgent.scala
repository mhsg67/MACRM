package ca.usask.agents.macrm.nodemanager.agents

import ca.usask.agents.macrm.nodemanager.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import org.joda.time.DateTime
import akka.actor._

class NodeMonitorAgent(val nodeManager: ActorRef) extends Agent {

    import context.dispatcher
    override def preStart() = context.system.scheduler.scheduleOnce(NodeManagerConfig.heartBeatStartDelay, self, "heartBeatEvent")

    val serverState = ServerState.apply()

    def receive = {
        case "initiateEvent"  => Event_initiate()
        case "heartBeatEvent" => Event_heartBeat()
        case _                => Handle_UnknownMessage
    }

    def Event_initiate() = {
        Logger.Log("NodeMonitorAgent Initialization")
    }

    /**
     * Maybe that self scheduled message lose then you should have 
     * recovery mechanism, maybe sending to message each after NodeManagerConfig.heartBeatInterval*2 
     */
    def Event_heartBeat() = {
        nodeManager ! create_HeartBeat(createNodeReport())
        context.system.scheduler.scheduleOnce(NodeManagerConfig.heartBeatInterval, self, "heartBeatEvent")
    }

    def createNodeReport(): NodeReport = serverState.getServerStatus(nodeManager, null)

    def create_HeartBeat(_nodeReport: NodeReport) = new _HeartBeat(self, DateTime.now(), _nodeReport)

}