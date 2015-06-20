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
        val file = scala.io.Source.fromFile("ConfigNodeManager.txt")
        val lines = file.getLines()
        lines.map(x => x.split(":")(1)).toList
    }

    /*def getNodeResource(index: Int) = index match {
        case x1 if 0 until 535 contains x1   => new Resource(8000, 6)
        case x2 if 535 until 842 contains x2 => new Resource(4000, 6)
        case x3 if 842 until 922 contains x3 => new Resource(12000, 6)
        case x4 if 922 until 986 contains x4 => new Resource(16000, 12)
        case x5 if 986 until 996 contains x5 => new Resource(4000, 3)
        case _                               => new Resource(2000, 6)
    }*/

    /*def getNodeResource(index: Int) = index match {
        case x1 if 0 until 268 contains x1   => new Resource(8000, 6)
        case x2 if 268 until 421 contains x2 => new Resource(4000, 6)
        case x3 if 421 until 461 contains x3 => new Resource(12000, 6)
        case x4 if 461 until 493 contains x4 => new Resource(16000, 12)
        case x5 if 493 until 498 contains x5 => new Resource(4000, 3)
        case _                               => new Resource(2000, 6)
    }*/

    def getNodeResource(index: Int) = index match {
        case x1 if 0 until 108 contains x1   => new Resource(8000, 6)
        case x2 if 108 until 169 contains x2 => new Resource(4000, 6)
        case x3 if 169 until 185 contains x3 => new Resource(12000, 6)
        case x4 if 185 until 198 contains x4 => new Resource(16000, 12)
        case _                               => new Resource(4000, 3)
    }

    def main(args: Array[String]) {
        val startIndex = args(0).toInt
        val numActor = args(1).toInt
        val generalConfig = readConfiguration()

        NodeManagerConfig.heartBeatInterval = generalConfig(4).toInt
        NodeManagerConfig.resourceTrackerIPAddress = generalConfig(1)
        JobManagerConfig.numberOfAllowedSamplingRetry = generalConfig(3).toInt
        JobManagerConfig.samplingTimeoutLong = generalConfig(2).toLong
        println("timeout " + JobManagerConfig.samplingTimeoutLong)
        JobManagerConfig.resourceTrackerIPAddress = generalConfig(1)
        JobManagerConfig.clusterManagerIPAddress = generalConfig(0)

        val initialLoads = generalConfig.drop(5).map(x => { val y = x.split(','); (y(0).toFloat, y(1).toFloat) })
        var count = startIndex

        val actorsList = (startIndex until startIndex + numActor toList).map(y => ActorSystem.create("NodeManagerAgent", ConfigFactory.load().getConfig("NodeManagerAgent")).actorOf(Props(new SimulationNodeManagerAgent(y)), name = "NodeManagerAgent"))
        actorsList.foreach(x => {
            val nodeResource = getNodeResource(count)
            val initialLoad = new Resource(initialLoads(count - 1)._1, initialLoads(count - 1)._2)
            x ! new _NodeManagerSimulationInitiate(nodeResource, initialLoad, List())
            count = count + 1
        })
    }
}
