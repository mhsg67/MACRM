package ca.usask.agents.macrm.common.records

class Resource(var memory: Int = 0, var virtualCore: Int = 0) extends AgentsComaparable[Resource] {
    override def ==(other: Resource): Boolean = {
        if (this.memory == other.memory && this.virtualCore == other.virtualCore)
            return true;
        else
            return false;
    }

    override def toString(): String = return "<memory:" + memory.toString() + ", vCores:" + virtualCore.toString() + ">"

    def +(other: Resource): Resource = {
        new Resource(this.memory + other.memory, this.virtualCore + other.virtualCore)
    }

    def -(other: Resource): Resource = {
        new Resource(this.memory - other.memory, this.virtualCore - other.virtualCore)
    }

}