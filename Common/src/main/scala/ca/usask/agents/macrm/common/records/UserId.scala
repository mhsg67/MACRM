package ca.usask.agents.macrm.common.records

import java.io.Serializable

@SerialVersionUID(100L)
class UserId(val id: Int) extends AgentsComaparable[UserId] with Serializable {
    override def equal(other: UserId) = if (this.id == other.id) true else false
}