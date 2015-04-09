package ca.usask.agents.macrm.clustermanager.agents

import akka.actor._
import ca.usask.agents.macrm.clustermanager.utils._
import com.typesafe.config.ConfigFactory

/**
 * It is the starter for ClusterManager
 */
object main {

    def readConfiguration(): List[String] = {
        val file = scala.io.Source.fromFile("ClusterManagerConfig.txt")
        val lines = file.getLines()
        lines.map(x => x.split(":")(1)).toList
    }

    def main(args: Array[String]) {
        val generalConfig = readConfiguration()

        ClusterManagerConfig.clusterManagerIpAddress = generalConfig(1)

        val system = ActorSystem.create("ClusterManagerAgent", ConfigFactory.load().getConfig("ClusterManagerAgent"))
        val clusterManagerAgent = system.actorOf(Props[ClusterManagerAgent], name = "ClusterManagerAgent")

        clusterManagerAgent ! "initiateEvent"
    }
}