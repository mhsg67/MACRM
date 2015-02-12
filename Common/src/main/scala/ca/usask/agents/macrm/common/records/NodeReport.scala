package ca.usask.agents.macrm.common.records

import org.joda.time.DateTime
import java.io.Serializable

@SerialVersionUID(100L)
class NodeReport(val nodeId: NodeId,
                 val rackName: String,
                 val used: List[(UserId, Resource)], 
                 val capability: Resource,
                 val utilization: Utilization,
                 val reportTime: DateTime,
                 val nodeState: NodeState,
                 val nodeQueueState: NodeQueueState) extends Serializable{
    override def toString() = "<nodeId:" + nodeId.toString() + " rackName:" + rackName + " capablity:" + capability.toString() + ">" 
}
