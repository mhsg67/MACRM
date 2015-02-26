package ca.usask.agents.macrm.common.records

import java.io.Serializable

object Priority {
    val UNDEFINED: Int = -1
}

@SerialVersionUID(100L)
class Priority(var priority: Int)
    extends Serializable {

    override def equals(input: Any): Boolean = input match {
        case that: Priority => this.priority == that.priority
        case _              => false
    }

    override def toString(): String = return priority.toString()
}
