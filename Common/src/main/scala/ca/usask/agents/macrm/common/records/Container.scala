package ca.usask.agents.macrm.common.records

import java.io.Serializable

@SerialVersionUID(100L)
class Container(var containerId: ContainerId,
                var nodeId: NodeId,
                var resource: Resource,
                var priority: Priority) extends AgentsComaparable[Container] with Serializable {
    override def equal(other: Container): Boolean = {
        if (this.nodeId == other.nodeId && this.resource == other.resource)
            return true;
        else
            return false;
    }
}
