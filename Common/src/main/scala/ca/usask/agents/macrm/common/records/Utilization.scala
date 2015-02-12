package ca.usask.agents.macrm.common.records

import java.io.Serializable

@SerialVersionUID(100L)
class Utilization(val memoryUtilization: Double = 0.0, val virtualCoreUtilization: Double = 0.0, val numberOfContainers: Int = 1)
    extends AgentsComaparable[Utilization] with Serializable {

    override def equal(other: Utilization): Boolean = {
        if (this.memoryUtilization == other.memoryUtilization &&
            this.virtualCoreUtilization == other.virtualCoreUtilization &&
            this.numberOfContainers == other.numberOfContainers)
            return true;
        else
            return false;
    }

}
