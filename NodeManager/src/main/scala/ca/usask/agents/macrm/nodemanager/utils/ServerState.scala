package ca.usask.agents.macrm.nodemanager.utils

import akka.actor._
import ca.usask.agents.macrm.common.records._
import org.joda.time.DateTime

/**
 * local test and duties that NodeManager should perform
 *
 * TODO: Lock at Hadoop to get more idea
 */
trait ServerState {

    def initializeServer(): Boolean

    def getServerResource(): Resource

    def getServerFreeResources(): Resource

    def getServerStatus(nodeManager: ActorRef, nodeQueueState: Int): NodeReport

    def initializeSimulationServer(resource: Resource, capability: List[Constraint]): Boolean

}

/**
 * This is just class factory to create either of above classes depends on simulation or real case
 */
object ServerState {
    def apply(): ServerState = if (NodeManagerConfig.isSimulation) SimulationServerState else RealServerState
}