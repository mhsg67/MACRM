package ca.usask.agents.macrm.nodemanager.utils

import akka.actor._
import ca.usask.agents.macrm.common.records._
import org.joda.time.DateTime

trait ServerState {

    def initializeServer(): Boolean

    def getServerResource(): Resource

    def getServerFreeResources(): Resource

    def getServerStatus(nodeManager: ActorRef): NodeReport

    def initializeSimulationServer(resource: Resource, capability: List[Constraint]): Boolean

    def createContainer(userId: Int, jobId: Long, taskIndex: Int, size: Resource): Option[Long]

    def killContainer(containerId: Long): Option[Int]

}


object ServerState {
    def apply(isSimulation: Boolean): ServerState = if (isSimulation) new SimulationServerState() else RealServerState
}