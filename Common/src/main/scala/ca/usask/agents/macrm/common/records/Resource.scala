package ca.usask.agents.macrm.common.records

import java.io.Serializable

@SerialVersionUID(100L)
class Resource(val memory: Int = 0, val virtualCore: Int = 0) extends AgentsComaparable[Resource] with Serializable {
    override def equal(other: Resource): Boolean = {
        if (this.memory == other.memory && this.virtualCore == other.virtualCore)
            return true;
        else
            return false;
    }

    override def toString(): String = return "<memory:" + memory.toString() + ", vCores:" + virtualCore.toString() + ">"

    def isNotUsable() = if (this.memory == 0 || this.virtualCore == 0) true else false

    def +(other: Resource) = new Resource(this.memory + other.memory, this.virtualCore + other.virtualCore)

    def -(other: Resource) = new Resource(this.memory - other.memory, this.virtualCore - other.virtualCore)

    def <(other: Resource) = if (other.memory >= this.memory && other.virtualCore >= this.virtualCore) true else false

    def >(other: Resource) = if (other.memory <= this.memory && other.virtualCore <= this.virtualCore) true else false

}