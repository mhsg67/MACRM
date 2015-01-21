package ca.usask.agents.macrm.common.records

class UserId(val id:Int) extends  AgentsComaparable[UserId]{
    override def equal(other:UserId) = if(this.id == other.id) true else false
}