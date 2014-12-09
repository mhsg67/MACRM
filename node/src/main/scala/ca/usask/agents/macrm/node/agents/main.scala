package ca.usask.agents.macrm.node.agents

import akka.actor._
import ca.usask.agents.macrm.node.utils._

/**
 * It is the starter for node manager; this will create a NodeManagerAgent
 * and all the other services will be handle by that actor
 */
object main extends App {
    try {
        NodeConfig.readConfigurationFile()

        val system = ActorSystem("NodeManager")
        val nodeManager = system.actorOf(Props[NodeManagerAgent], name = "nodeManager")

        nodeManager ! "initiateEvent"
    }
    catch {
        case e: Exception => Logger.Error(e.toString())
    }
}