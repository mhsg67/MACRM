package ca.usask.agents.macrm.nodemanager.utils

import scala.concurrent.duration._
import org.joda.time._

/**
 * Holds the system configuration parameters for node manager project
 */
object NodeManagerConfig {
    
    val isSimulation = true

    def readConfigurationFile() = {
        Logger.Log("Start reading configuration file")
        Logger.Log("Finished reading configuration file")
    }

    def getResourceTrackerAddress() = "akka.tcp://ResourceTrackerAgent@" +
        trackerIPAddress + ":" +
        trackerDefualtPort + "/" +
        "user/ResourceTrackerAgent"

    /**
     * To access resourceTracker actor
     */
    val trackerIPAddress = "127.0.1.1"
    val trackerDefualtPort = "3000"

    /**
     * Based on YARN configuration we set heart beat interval
     * to RM to 1000
     */
    val heartBeatInterval = 1000 millis

    /**
     * Each node start to send heart beat 3000 millisecond
     * after booting
     */
    val heartBeatStartDelay = 2000 millis

}
