package ca.usask.agents.macrm.nodemanager.agents

import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import akka.actor._
import ca.usask.agents.macrm.nodemanager.utils._
import ca.usask.agents.macrm.jobmanager.utils._
import com.typesafe.config.ConfigFactory
import java.net._

/**
 * It is the starter for node manager; this will create a NodeManagerAgent
 * and all the other services will be handle by that actor
 */
object main {

    def readConfiguration(): List[String] = {
        val file = scala.io.Source.fromFile("NodeManagerConfig.txt")
        val lines = file.getLines()
        lines.map(x => x.split(":")(1)).toList
    }

    def main(args: Array[String]) {
        val startIndex = args(0).toInt
        val numActor = args(1).toInt
        val generalConfig = readConfiguration()

        NodeManagerConfig.heartBeatInterval = generalConfig(4).toInt
        NodeManagerConfig.resourceTrackerIPAddress = generalConfig(1)
        JobManagerConfig.numberOfAllowedSamplingRetry = generalConfig(3).toInt
        JobManagerConfig.samplingTimeoutLong = generalConfig(2).toLong
        JobManagerConfig.resourceTrackerIPAddress = generalConfig(1)
        JobManagerConfig.clusterManagerIPAddress = generalConfig(0)

        val actorsList = (startIndex until startIndex + numActor toList).map(y => ActorSystem.create("NodeManagerAgent", ConfigFactory.load().getConfig("NodeManagerAgent")).actorOf(Props(new SimulationNodeManagerAgent(y)), name = "NodeManagerAgent"))
        var count = startIndex
        actorsList.foreach(x => {
            if(count<535)
                x ! new _NodeManagerSimulationInitiate(new Resource(8000, 6), List())
            else if(count>=535 && count<842)
                x ! new _NodeManagerSimulationInitiate(new Resource(4000, 6), List())
            else if(count>=842 && count<922)
                x ! new _NodeManagerSimulationInitiate(new Resource(12000, 6), List())
            else if(count>=922 && count<986)
                x ! new _NodeManagerSimulationInitiate(new Resource(16000, 12), List())
            else if(count>=986 && count<996)
                x ! new _NodeManagerSimulationInitiate(new Resource(4000, 3), List())
            else
                x ! new _NodeManagerSimulationInitiate(new Resource(2000, 6), List())
            count = count + 1
        })

    }
}
