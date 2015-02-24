package ca.usask.agents.macrm.common.records

import java.io.Serializable

@SerialVersionUID(100L)
class NodeQueueState(var totalNumRequest: Int = 0,
                     var totalNumRandomRequest: Int = 0)
    extends AgentsComaparable[NodeQueueState] with Serializable {

    override def equal(input: Any): Boolean = input match {
        case that: NodeQueueState => this.totalNumRequest == that.totalNumRequest &&
            this.totalNumRandomRequest == that.totalNumRandomRequest
        case _ => false
    }
}
