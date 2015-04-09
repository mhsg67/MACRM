package ca.usask.agents.macrm.resourcetracker.agents

import akka.actor._
import ca.usask.agents.macrm.resourcetracker.utils._
import com.typesafe.config.ConfigFactory

/**
 * It is the starter for resource finder agent
 */
object main {

    def readConfiguration(): List[String] = {
        val file = scala.io.Source.fromFile("ResourceTrackerConfig.txt")
        val lines = file.getLines()
        lines.map(x => x.split(":")(1)).toList
    }

    def main(args: Array[String]) {
        val generalConfig = readConfiguration()

        ResourceTrakerConfig.clusterManagerIPAddress = generalConfig(0)
        ResourceTrakerConfig.samplingSuccessProbability = generalConfig(1).toDouble

        val system = ActorSystem.create("ResourceTrackerAgent", ConfigFactory.load().getConfig("ResourceTrackerAgent"))
        val resourceTracker = system.actorOf(Props[ResourceTrackerAgent], name = "ResourceTrackerAgent")

        resourceTracker ! "initiateEvent"
    }
}