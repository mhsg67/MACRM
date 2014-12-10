package ca.usask.agents.macrm.tracker.agents

import akka.actor._
import ca.usask.agents.macrm.tracker.utils._

/**
 * It is the starter for resource finder agent
 */
object Main {
    def main(args: Array[String]): Unit = {
        try {
            TrackerConfig.readConfigurationFile()

            val system = ActorSystem("ResourceTracker")
            val resourceTracker = system.actorOf(Props[ResourceTrackerAgent], name = "resourceTracker")

            resourceTracker ! "initiateEvent"
        }
        catch {
            case e: Exception => Logger.Error(e.toString())
        }
    }
}