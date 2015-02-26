package ca.usask.agents.macrm.nodemanager.utils

import akka.actor._
import ca.usask.agents.macrm.common.records._
import org.joda.time.DateTime

/**
 * This class is used for simulation in which there is no real server
 */
object SimulationServerState extends ServerState {

    var serverResource: Resource = null
    var serverCapability: List[Constraint] = null
    var serverUsed: List[(Int, Resource)] = null //That Int represent UserId
    val serverNodeState = NodeState("RUNNING")

    def initializeSimulationServer(resource: Resource, capability: List[Constraint]) = {
        serverResource = resource
        serverCapability = capability
        true
    }

    def getServerStatus(_nodeManager: ActorRef, _nodeQueueState: NodeQueueState) = new NodeReport(
        NodeId(_nodeManager),
        "No Rack",
        serverUsed,
        serverResource,
        serverCapability,
        getServerUtilization(),
        DateTime.now(),
        getServerNodeState,
        _nodeQueueState)

    def getServerUtilization(): Utilization = {
        val tempResource = serverUsed.foldLeft(new Resource(0, 0))((x, y) => x + y._2)
        new Utilization(tempResource.memory, tempResource.virtualCore, serverUsed.length)
    }

    def getServerAvailableResources(): Resource = serverResource - serverUsed.foldLeft(new Resource(0, 0))((x, y: (Int, Resource)) => x + y._2)

    def getServerCapability(): Resource = serverResource

    def getServerNodeState(): NodeState = serverNodeState

    def initializeServer(): Boolean = true
}