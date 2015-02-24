package ca.usask.agents.macrm.common.records

import java.io.Serializable

@SerialVersionUID(100L)
class Container(var containerId: ContainerId,
                var nodeId: NodeId,
                var resource: Resource,
                var priority: Priority)
    extends AgentsComaparable[Container] with Serializable {

    override def equal(input: Any): Boolean = input match {
        case that: Container => this.nodeId.equal(that.nodeId) && this.resource.equal(that.resource)
        case _               => false
    }
}
