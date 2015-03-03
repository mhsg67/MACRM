package ca.usask.agents.macrm.nodemanager.agents

import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
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

        //nodeManager ! "initiateEvent"
        nodeManager ! new _NodeManagerSimulationInitiate(new Resource(4000,4), List())
    }
    catch {
        case e: Exception => Logger.Error(e.toString())
    }
}
