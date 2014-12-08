package ca.usask.agents.macrm.common.records

import org.joda.time.DateTime

class NodeReport(var nodeId: NodeId,
                 var rackName: String,
                 var used: Resource,
                 var capability: Resource,
                 var utilization: Utilization,
                 var reportTime: DateTime,                 
                 var nodeState:NodeState.Value,
                 var nodeQueueState:NodeQueueState) 