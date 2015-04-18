package ca.usask.agents.macrm.resourcetracker.utils

import ca.usask.agents.macrm.common.records._
import scala.concurrent.duration._

/**
 * Holds the system configuration parameters for resource tracker project
 */
object ResourceTrakerConfig {

    def readConfigurationFile() = {
        Logger.Log("Start reading configuration file")
        Logger.Log("Finished reading configuration file")
    }

    def getClusterManagerAddress() = {
        val result = "akka.tcp://ClusterManagerAgent@" + clusterManagerIPAddress + ":" + clusterManagerAgentDefualtPort + "/user/ClusterManagerAgent"
        println(result)
        result
    }

    /**
     * If we do not receive heart beat from a node during last 2 minutes we
     * assume that node had died
     * IN MILLISECOND
     */
    val heatbeatTimeOut = 2 * 60 * 1000

    /**
     * To access ClusterManager actor
     */
    var clusterManagerIPAddress = "127.0.0.1"
    val clusterManagerAgentDefualtPort = "2000"
    
    /**
     * Minimum resources for a JobManager container
     */
     val minMemory = 500
     val minVirtualCore = 1
     val minResourceForJobManager = new Resource(minMemory,minVirtualCore)
}