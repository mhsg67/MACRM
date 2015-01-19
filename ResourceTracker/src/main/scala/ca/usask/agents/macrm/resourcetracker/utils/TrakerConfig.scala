package ca.usask.agents.macrm.resourcetracker.utils

/**
 * Holds the system configuration parameters for resource tracker project
 */
object TrackerConfig {

    def readConfigurationFile() = {
        Logger.Log("Start reading configuration file")
        Logger.Log("Finished reading configuration file")
    }

    /**
     * If we do not receive heart beat from a node during last 2 minutes we
     * assume that node had died
     * IN MILLISECOND
     */
    var heatbeatTimeOut = 2 * 60 * 1000

}
