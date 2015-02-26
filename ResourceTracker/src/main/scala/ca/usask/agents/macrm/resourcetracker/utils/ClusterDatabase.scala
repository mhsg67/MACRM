package ca.usask.agents.macrm.resourcetracker.utils

import ca.usask.agents.macrm.common.records._
import org.joda.time._

case class nodeIdToNodeStateRow(var totalResource: Resource, 
        var usedResources:Resource, 
        var capabilities:List[Constraint],
        var utilization:Utilization,
        var queueState: Int,
        var lastReportTime: DateTime) 
        
     

object ClusterDatabase {

    var nodeIdToNodeStateTable = Map[NodeId,nodeIdToNodeStateRow]()
    
    //var nodeIdTo
    
}