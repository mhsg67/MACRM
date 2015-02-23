package ca.usask.agents.macrm.common.records

import org.joda.time.DateTime
import java.io.Serializable

@SerialVersionUID(100L)
class NodeReport(val nodeId: NodeId,
                 val rackName: String,
                 val used: List[(Int, Resource)], //That Int reperesents UserId
                 val capability: Resource,
                 val otherCapablity: List[Constraint],
                 val utilization: Utilization,
                 val reportTime: DateTime,
                 val nodeState: NodeState,
                 val nodeQueueState: NodeQueueState) extends Serializable {

    def getAvailableResources() = capability - used.foldLeft(new Resource(0, 0))((x, y) => y._2 + x)

    override def toString() = "<nodeId:" + nodeId.toString() + " rackName:" + rackName + " capablity:" + capability.toString() + ">"
}
