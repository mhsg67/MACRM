package ca.usask.agents.macrm.nodemanager.utils

import akka.actor._
import ca.usask.agents.macrm.common.records._
import org.joda.time.DateTime

/**
 * This class is used for the real cases when we really need to execute scripts
 */
object RealServerState extends ServerState {

    def initializeServer() = false
    
    def initializeSimulationServer(resource:Resource, capability:List[Constraint]) = false

    def getServerStatus(nodeManager: ActorRef, nodeQueueState: Int) = null

    def getServerFreeResources(): Resource = null
    
    def getServerResource(): Resource = null
}