package ca.usask.agents.macrm.center.utils


/**
 * Holds the system configuration parameters for central resource manager project
 */
object CenterConfig {

    def readConfigurationFile() = {
        Logger.Log("Start reading configuration file")
        Logger.Log("Finished reading configuration file")        
    }
    
    /**
     * The default system queue is FIFO Queue
     */
    def QueueType = "FIFOQueue"
}