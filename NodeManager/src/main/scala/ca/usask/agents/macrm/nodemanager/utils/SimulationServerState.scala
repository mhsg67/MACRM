package ca.usask.agents.macrm.nodemanager.utils

import akka.actor._
import ca.usask.agents.macrm.common.records._
import org.joda.time.DateTime

/**
 * This class is used for simulation in which there is no real server
 */
object SimulationServerState extends ServerState {

    var nextContainerId = 0
    var serverResource: Resource = null
    var serverCapabilities: List[Constraint] = null
    var serverContainers: List[Container] = null
    var serverNodeState = NodeState("RUNNING")

    def initializeServer(): Boolean = true

    def initializeSimulationServer(resource: Resource, capability: List[Constraint]) = {
        serverResource = resource
        serverCapabilities = capability
        true
    }

    def getServerStatus(nodeManager: ActorRef) =
        new NodeReport(NodeId(nodeManager), serverResource, serverCapabilities,
            serverContainers, serverUtilization, serverNodeState, 0)

    def serverUtilization(): Utilization = new Utilization(0.0, 0.0)

    def getServerFreeResources = serverResource - serverContainers.foldLeft(new Resource(0, 0))((x, y) => y.resource + x)

    def getServerResource() = serverResource

    def createContainer(userId: Int, jobId: Long, taskIndex: Int, size: Resource): Option[Int] = {
        if ((serverResource - serverContainers.foldLeft(new Resource(0, 0))((x, y) => y.resource + x)) < size)
            None
        else {
            serverContainers = new Container(nextContainerId, userId, jobId, taskIndex, size) :: serverContainers
            nextContainerId += 1
            Some(nextContainerId - 1)
        }
    }

}