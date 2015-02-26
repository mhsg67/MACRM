package ca.usask.agents.macrm.common.records

import java.io.Serializable

@SerialVersionUID(100L)
class Container(var containerId: ContainerId,
                var nodeId: NodeId,
                var resource: Resource,
                var priority: Priority)
    extends Serializable {

    override def equals(input: Any): Boolean = input match {
        case that: Container => this.nodeId == that.nodeId && this.resource == that.resource
        case _               => false
    }
}
