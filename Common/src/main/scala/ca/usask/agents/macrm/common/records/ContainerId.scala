package ca.usask.agents.macrm.common.records

class ContainerId(val id:Long = -1) extends AgentsComaparable[ContainerId]{
    
    override def equal(other:ContainerId):Boolean = {
        if(this.id == other.id)
            return true;
        else
            return false;
    } 

}
