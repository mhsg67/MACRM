package ca.usask.agents.macrm.nodemanager.utils

import akka.actor._
import ca.usask.agents.macrm.common.records._
import org.joda.time.DateTime

/**
 * This class is used for simulation in which there is no real server
 */
object SimulationServerState extends ServerState {

    val serverCapability = new Resource(1000, 3)
    val serverNodeState = NodeState("RUNNING")
    val serverUsed:List[(Int,Resource)] = List() //That Int represent UserId

    //def getServerStatus(_nodeManager: ActorRef, _nodeQueueState: NodeQueueState) = new NodeReport("NoRack")
    
    def getServerStatus(_nodeManager: ActorRef, _nodeQueueState: NodeQueueState) = new NodeReport(
        NodeId(_nodeManager),
        "No Rack",
        serverUsed,
        serverCapability,
        null,
        getServerUtilization(),
        DateTime.now(),
        getServerNodeState,
        _nodeQueueState)

    def getServerUtilization(): Utilization = {
        val tempResource = serverUsed.foldLeft(new Resource(0, 0))((x, y) => x + y._2)
        new Utilization(tempResource.memory, tempResource.virtualCore, serverUsed.length)
    }

    def getServerAvailableResources(): Resource = serverCapability - serverUsed.foldLeft(new Resource(0, 0))((x, y: (Int, Resource)) => x + y._2)

    def getServerCapability(): Resource = serverCapability

    def getServerNodeState(): NodeState = serverNodeState

    def initializeServer(): Boolean = true
}