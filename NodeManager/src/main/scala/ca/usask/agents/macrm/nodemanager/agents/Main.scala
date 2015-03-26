package ca.usask.agents.macrm.nodemanager.agents

import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import akka.actor._
import ca.usask.agents.macrm.nodemanager.utils._
import ca.usask.agents.macrm.jobmanager.utils._
import com.typesafe.config.ConfigFactory

/**
 * It is the starter for node manager; this will create a NodeManagerAgent
 * and all the other services will be handle by that actor
 */
object main {

    def readConfiguration(): List[String] = {
        val stream = getClass.getResourceAsStream("/configuration.txt")
        val lines = scala.io.Source.fromInputStream(stream).getLines
        val result = lines.map(x => x.split(":")(1)).toList
        result
    }

    def main(args: Array[String]) {  
        val startIndex = args(0).toInt
        val config = readConfiguration()
        
        NodeManagerConfig.resourceTrackerIPAddress = config(1)
        JobManagerConfig.resourceTrackerIPAddress = config(1)
        JobManagerConfig.clusterManagerIPAddress = config(0)
        val numActor = config(2).toInt
        val actorsList = (startIndex until startIndex + numActor toList).map(y => ActorSystem.create("NodeManagerAgent", ConfigFactory.load().getConfig("NodeManagerAgent")).actorOf(Props(new NodeManagerAgent(y)), name = "NodeManagerAgent"))
        actorsList.foreach(x => x ! new _NodeManagerSimulationInitiate(new Resource(4000, 4), List()))

    }
}
