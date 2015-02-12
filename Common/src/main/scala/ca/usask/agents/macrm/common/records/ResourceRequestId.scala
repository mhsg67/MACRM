package ca.usask.agents.macrm.common.records

class ResourceRequestId(val jobId:String) extends AgentsComaparable[ResourceRequestId] {

    val id = GUIDGenerator.getNextGUID;    

    override def equal(other: ResourceRequestId): Boolean = {
        if (this.id == other.id)
            return true;
        else
            return false;
    }

    override def toString(): String = jobId + "::" + id
}
