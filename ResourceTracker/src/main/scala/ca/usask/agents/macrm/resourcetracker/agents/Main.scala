package ca.usask.agents.macrm.resourcetracker.agents

import akka.actor._
import ca.usask.agents.macrm.resourcetracker.utils._
import com.typesafe.config.ConfigFactory

/**
 * It is the starter for resource finder agent
 */
object main {

    def readConfiguration(): List[String] = {
        val stream = getClass.getResourceAsStream("/configuration.txt")
        val lines = scala.io.Source.fromInputStream(stream).getLines()
        val result = lines.map(x => x.split(":")(1)).toList
        result
    }

    def main(args: Array[String]) {
        try {
            val config = readConfiguration()
            ResourceTrakerConfig.clusterManagerIPAddress = config(0)

            val system = ActorSystem.create("ResourceTrackerAgent", ConfigFactory.load().getConfig("ResourceTrackerAgent"))
            val resourceTracker = system.actorOf(Props[ResourceTrackerAgent], name = "ResourceTrackerAgent")

            resourceTracker ! "initiateEvent"

        } catch {
            case e: Exception => Logger.Error(e.toString())
        }
    }
}