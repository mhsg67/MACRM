package ca.usask.agents.macrm.common.records

class ResourceRequestId(var jobId:String) extends AgentsComaparable[ResourceRequestId] {

    var id = GUIDGenerator.GetNextGUID;    

    override def equal(other: ResourceRequestId): Boolean = {
        if (this.id == other.id)
            return true;
        else
            return false;
    }

    override def toString(): String = jobId + "::" + id
}
