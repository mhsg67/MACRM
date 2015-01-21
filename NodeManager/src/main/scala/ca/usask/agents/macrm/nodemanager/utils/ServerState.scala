package ca.usask.agents.macrm.nodemanager.utils

import akka.actor._

/**
 * local test and duties that NodeManager should perform
 * 
 * TODO: Lock at Hadoop to get more idea
 * def initializeServer()
 * def getServerStatus() 
 * def createContainer()
 * def removeContainer()
 */
abstract class ServerState {
}

/**
 * This class is used for the real cases when we really need to execute scripts 
 * 
 * TODO: You should create an actor for execution of script since they are blocking
 */
class RealServerState extends ServerState {
}

/**
 * This class is used for simulation in which there is no real server
 */
class SimulationServerState extends ServerState {
}

/**
 * This is just class factory to create either of above classes depends on simulation or real case
 */
object ServerState {
    def apply(): ServerState = if (NodeManagerConfig.isSimulation) new SimulationServerState else new RealServerState
}