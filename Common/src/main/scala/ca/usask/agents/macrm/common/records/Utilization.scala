package ca.usask.agents.macrm.common.records

import java.io.Serializable

@SerialVersionUID(100L)
class Utilization(val memoryUtilization: Double = 0.0, val virtualCoreUtilization: Double = 0.0, val numberOfContainers: Int = 1)
    extends AgentsComaparable[Utilization] with Serializable {

    override def equal(input: Any): Boolean = input match {
        case that: Utilization => this.memoryUtilization == that.memoryUtilization &&
            this.virtualCoreUtilization == that.virtualCoreUtilization &&
            this.numberOfContainers == that.numberOfContainers
        case _ => false
    }

}
