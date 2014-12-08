package MACRM.finder

import ca.usask.agents.macrm.common._
import org.joda.time._

/**
 * Holds the system configuration parameters for resource finding project
 */
object FinderConfig {
    
    /**
     * If we do not receive heart beat from a node during last 2 minutes we
     * assume that node had died
     * IN MILLISECOND
     */
    var heatbeatTimeOut = 2*60*1000 

}
