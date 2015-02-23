package ca.usask.agents.macrm.nodemanager.agents

import akka.actor._
import ca.usask.agents.macrm.nodemanager.utils._
import com.typesafe.config.ConfigFactory

/**
 * It is the starter for node manager; this will create a NodeManagerAgent
 * and all the other services will be handle by that actor
 */
object main extends App {
    try {
        NodeManagerConfig.readConfigurationFile()

        val system = ActorSystem.create("NodeManagerAgent", ConfigFactory.load().getConfig("NodeManagerAgent"))
        val nodeManager = system.actorOf(Props[NodeManagerAgent], name = "NodeManagerAgent")

        nodeManager ! "initiateEvent"
    }
    catch {
        case e: Exception => Logger.Error(e.toString())
    }
}