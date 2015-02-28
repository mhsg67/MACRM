package ca.usask.agents.macrm.nodemanager.agents

import ca.usask.agents.macrm.nodemanager.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import org.joda.time.DateTime
import scala.concurrent.duration._
import akka.actor._

class ServerStateAgent(val nodeManager: ActorRef) extends Agent {

    var lastSubmissionOfHeartBeat = DateTime.now()

    val serverState = ServerState.apply()

    def receive = {
        case "initiateEvent"                         => Event_initiate
        case "heartBeatEvent"                        => Handle_heartBeat(sender())
        case "checkContainersEvent"                  => Handle_checkContainers(sender())
        case "checkAvailableResourcesEvent"          => Handle_checkAvailableResources(sender())
        case message: _NodeManagerSimulationInitiate => Event_NodeManagerSimulationInitiate(message)
    }

    def Event_initiate = {
        Logger.Log("ServerStateAgent Initialization")
    }

    def Event_NodeManagerSimulationInitiate(message: _NodeManagerSimulationInitiate) = {
        Logger.Log("ServerStateAgent Initialization")

        serverState.initializeSimulationServer(message.resource, message.capabilities)
    }

    def Handle_heartBeat(_sender: ActorRef) = {
        _sender ! create_HeartBeat(createNodeReport())
        lastSubmissionOfHeartBeat = DateTime.now()
    }

    def Handle_checkAvailableResources(sender: ActorRef) =
        if (shouldServerNowOrWaitForHeartBeatResponse())
            sender ! new _Resource(serverState.getServerFreeResources())
        else
            sender ! new _Resource(new Resource(0,0))

    def shouldServerNowOrWaitForHeartBeatResponse(): Boolean = {
        if (DateTime.now().getMillis() - lastSubmissionOfHeartBeat.getMillis() < NodeManagerConfig.stopServingJobManagerRequestAfterHeartBeat)
            false
        else if (DateTime.now().getMillis() - lastSubmissionOfHeartBeat.getMillis() > NodeManagerConfig.stopServingJobManagerRequestBeforeHeartBeat)
            false
        else
            true
    }

    def createNodeReport(): NodeReport = serverState.getServerStatus(nodeManager)

    def create_HeartBeat(_nodeReport: NodeReport) = new _HeartBeat(self, DateTime.now(), _nodeReport)

    def Handle_checkContainers(_sender: ActorRef) = null
}