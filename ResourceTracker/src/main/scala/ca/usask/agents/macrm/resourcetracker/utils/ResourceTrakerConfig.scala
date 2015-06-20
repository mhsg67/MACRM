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
     val minMemory = 100
     val minVirtualCore = 0.2
     val minResourceForJobManager = new Resource(minMemory,minVirtualCore)
    
    /**
     * Delay for sending first _ClusterStateUpdate to the cluster manager
     */
    val firstClusterStateUpdateDelay = 25000 millis //3000 millis
    
    /**
     * Try to adapte sampling rate based on current cluster load
     * in order to achieve 1 - acceptableLevelOfSamplingFailure > 0.95 for 
     * finding resources
     * 
     * currentLoad ^ x = acceptableLevelOfSamplingFailure
     */
    val acceptableLevelOfSamplingFailure = 0.05
    
    /**
     * We try to increase sampling rate in a way that probability of finding free resources
     * in first sampling be greater than (1-samplingSuccessProbability)
     */
    var samplingSuccessProbability = 0.25 

}
