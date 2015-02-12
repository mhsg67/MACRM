package ca.usask.agents.macrm.common.records

import java.io.Serializable

@SerialVersionUID(100L)
class ContainerId(val id: Long = -1) extends AgentsComaparable[ContainerId] with Serializable {
    override def equal(other: ContainerId): Boolean = {
        if (this.id == other.id)
            return true;
        else
            return false;
    }

}
