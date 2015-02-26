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
    
    def initializeSimulationServer(resource:Resource, capability:List[Constraint]): Boolean

    def getServerStatus(_nodeManager: ActorRef, _nodeQueueState: NodeQueueState): NodeReport

    def getServerCapability(): Resource
    
    def getServerAvailableResources(): Resource
}


/**
 * This is just class factory to create either of above classes depends on simulation or real case
 */
object ServerState {
    def apply(): ServerState = if (NodeManagerConfig.isSimulation) SimulationServerState else RealServerState
}