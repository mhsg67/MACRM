package ca.usask.agents.macrm.nodemanager.utils

import scala.concurrent.duration._
import org.joda.time._

/**
 * Holds the system configuration parameters for node manager project
 */
object NodeManagerConfig {

    def getResourceTrackerAddress = "akka.tcp://ResourceTrackerAgent@" + resourceTrackerIPAddress + ":" + resourceTrackerDefualtPort + "/user/ResourceTrackerAgent"

    /**
     * To access resourceTracker actor
     */
    var resourceTrackerIPAddress = "127.0.0.1"
    val resourceTrackerDefualtPort = "3000"

    /**
     * Based on YARN configuration we set heart beat interval
     * to RM to 1000
     */
    var heartBeatInterval = 2000

    /**
     * Each node start to send heart beat 3000 millisecond
     * after booting
     */
    val heartBeatStartDelay = 1000
}
