package ca.usask.agents.macrm.common.records

object Priority extends AgentsComaparable[Priority] {
    val UNDEFINED: Int = -1
}

class Priority(var priority: Int) extends AgentsComaparable[Priority] {
    override def ==(other: Priority): Boolean = {
        if (this.priority == other.priority)
            return true;
        else
            return false;
    }

    override def toString(): String = return priority.toString()
}