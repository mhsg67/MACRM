package ca.usask.agents.macrm.common.records

import org.joda.time.DateTime

class NodeReport(val nodeId: NodeId,
                 val rackName: String,
                 val used: List[(UserId, Resource)], //for each user the total amount of resource that uses
                 val capability: Resource,
                 val utilization: Utilization,
                 val reportTime: DateTime,
                 val nodeState: NodeState.Value,
                 val nodeQueueState: NodeQueueState) 