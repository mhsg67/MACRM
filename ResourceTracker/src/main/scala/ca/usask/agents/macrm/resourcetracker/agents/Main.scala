package ca.usask.agents.macrm.resourcetracker.agents

import akka.actor._
import ca.usask.agents.macrm.resourcetracker.utils._
import com.typesafe.config.ConfigFactory

/**
 * It is the starter for resource finder agent
 */
object main extends App {
    try {
        ResourceTrakerConfig.readConfigurationFile()

        val system = ActorSystem.create("ResourceTrackerAgent", ConfigFactory.load().getConfig("ResourceTrackerAgent"))
        val resourceTracker = system.actorOf(Props[ResourceTrackerAgent], name = "ResourceTrackerAgent")

        resourceTracker ! "initiateEvent"
    }
    catch {
        case e: Exception => Logger.Error(e.toString())
    }
}