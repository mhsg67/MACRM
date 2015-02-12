package ca.usask.agents.macrm.common.records

import java.io.Serializable



object Priority {
    val UNDEFINED: Int = -1
}

@SerialVersionUID(100L)
class Priority(var priority: Int) extends AgentsComaparable[Priority] with Serializable {
    override def equal(other: Priority): Boolean = {
        if (this.priority == other.priority)
            return true;
        else
            return false;
    }

    override def toString(): String = return priority.toString()
}
