package ca.usask.agents.macrm.common.records

class Utilization(var memoryUtilization: Double = 0.0,
                  var virtualCoreUtilization: Double = 0.0,
                  var numberOfContainers: Int = 1)
    extends AgentsComaparable[Utilization] {
    override def ==(other: Utilization): Boolean = {
        if (this.memoryUtilization == other.memoryUtilization &&
            this.virtualCoreUtilization == other.virtualCoreUtilization &&
            this.numberOfContainers == other.numberOfContainers)
            return true;
        else
            return false;
    }

}