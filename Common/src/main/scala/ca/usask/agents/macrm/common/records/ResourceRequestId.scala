package ca.usask.agents.macrm.common.records

class ResourceRequestId(val jobId:String) extends AgentsComaparable[ResourceRequestId] {

    val id = GUIDGenerator.getNextGUID;    

    override def equal(input: Any): Boolean = input match{
        case that:ResourceRequestId => this.id == that.id
        case _ => false
    }

    override def toString(): String = jobId + "::" + id
}
