package ca.usask.agents.macrm.common.records

object NodeState extends Enumeration{
    val NEW, RUNNING, UNHEALTHY, DECOMMISSIONED, LOST, REBOOTED = Value
    
    def isUnusable():Boolean ={
        if(this == UNHEALTHY || this == DECOMMISSIONED || this == LOST)
            return true
        else
            return false;
    }
}