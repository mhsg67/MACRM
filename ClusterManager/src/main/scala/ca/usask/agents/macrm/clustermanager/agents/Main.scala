package ca.usask.agents.macrm.clustermanager.agents

import akka.actor._
import ca.usask.agents.macrm.clustermanager.utils._
import com.typesafe.config.ConfigFactory

/**
 * It is the starter for ClusterManager
 */
object main extends App {
    try {
        ClusterManagerConfig.readConfigurationFile()

        val system = ActorSystem.create("QueueAgent", ConfigFactory.load().getConfig("QueueAgent"))
        val queueAgent = system.actorOf(Props[QueueAgent], name = "QueueAgent")
        val userInterfaceAgent = system.actorOf(Props(new UserInterfaceAgent(queueAgent)), name = "UserInterfaceAgent")
        
        queueAgent ! "initiateEvent"        
        userInterfaceAgent ! "initiateEvent"

    }
    catch {
        case e: Exception => Logger.Error(e.toString())
    }
}