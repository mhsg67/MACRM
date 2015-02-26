package ca.usask.agents.macrm.common.records

import java.io.Serializable

@SerialVersionUID(100L)
class ContainerId(val id: Long = -1)
    extends Serializable {

    override def equals(input: Any): Boolean = input match {
        case that: ContainerId => this.id == that.id
        case _                 => false
    }

}
