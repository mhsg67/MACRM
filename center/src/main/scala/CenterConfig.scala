package MACRM.center

import MACRM.utility._

/**
 * Holds the system configuration parameters for central resource manager project
 */
object CenterConfig {

    /**
     * The default system queue is FIFO Queue
     */
    def QueueType = "FIFOQueue"

    //def QueueFactory[T](): BasicQueue = return BasicQueue     

}