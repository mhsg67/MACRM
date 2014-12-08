package ca.usask.agents.macrm.common.records

class Container(var containerId: ContainerId,
                var nodeId: NodeId,
                var resource: Resource,
                var priority: Priority) extends AgentsComaparable[Container] {
    override def ==(other: Container): Boolean = {
        if (this.nodeId == other.nodeId && this.resource == other.resource)
            return true;
        else
            return false;
    }
}