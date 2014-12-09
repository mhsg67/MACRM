package ca.usask.agents.macrm.node.utils

import scala.concurrent.duration._
import org.joda.time._

/**
 * Holds the system configuration parameters for node manager project
 */
object NodeConfig {

    def readConfigurationFile() = {
        Logger.Log("Start reading configuration file")
        Logger.Log("Finished reading configuration file")        
    }
    
    /**
     * Based on YARN configuration we set heart beat interval
     * to RM to 1000
     */
    var heartBeatInterval = 1000 millis

    /**
     * Each node start to send heart beat 3000 millisecond 
     * after booting
     */
    var heartBeatStartDelay = 3000 millis
    
}