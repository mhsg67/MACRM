package ca.usask.agents.macrm.common.records

import java.io.Serializable

@SerialVersionUID(100L)
class NodeQueueState(var totalNumRequest: Int = 0, var totalNumRandomRequest: Int = 0) extends AgentsComaparable[NodeQueueState] with Serializable {
    override def equal(other: NodeQueueState): Boolean = {
        if (this.totalNumRandomRequest == other.totalNumRandomRequest && this.totalNumRandomRequest == other.totalNumRandomRequest)
            return true;
        else
            return false;
    }
}
