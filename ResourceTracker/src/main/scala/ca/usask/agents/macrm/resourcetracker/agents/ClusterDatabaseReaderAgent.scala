package ca.usask.agents.macrm.resourcetracker.agents

import ca.usask.agents.macrm.resourcetracker.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import org.joda.time._
import akka.actor._
import java.util.Formatter.DateTime

class ClusterDatabaseReaderAgent(val resourceTrackerAgent: ActorRef) extends Agent {

    var isInCentralizeState = false
    var lastClusterUtilizationLevel = new Utilization(0.0, 0.0)
    var currentSamplingRate = 2.0

    def receive = {
        case "initiateEvent"       => Event_initiate()
        case message: _JMHeartBeat => Handle_JMHeartBeat(message)
        case message               => Handle_UnknownMessage("ClusterDatabaseReaderAgent", message)
    }

    def Event_initiate() = {
        Logger.Log("ClusterDatabaseReaderAgent Initialization")
    }

    def Handle_JMHeartBeat(message: _JMHeartBeat) = {
        val currentUtilization = ClusterDatabase.getCurrentClusterLoad()
        println(currentUtilization)

        val maxResourceUtilization = if (currentUtilization.memoryUtilization > currentUtilization.virtualCoreUtilization)
            currentUtilization.memoryUtilization
        else
            currentUtilization.virtualCoreUtilization

        if (maxResourceUtilization > 0.94) {
            resourceTrackerAgent ! "changeToCentralizedMode"
            isInCentralizeState = true
            currentSamplingRate = 2
        }
        else {
            if (maxResourceUtilization < 0.91 && isInCentralizeState == true) isInCentralizeState = false
            val properSamplingRate = calcProperSamplingRate(maxResourceUtilization)
            if (properSamplingRate != currentSamplingRate && properSamplingRate >= 2.0) {
                println("properSamplingRate " + properSamplingRate)
                currentSamplingRate = properSamplingRate
                resourceTrackerAgent ! new _ClusterState(self, DateTime.now(), currentSamplingRate, null, null, null, true)
            }
        }
    }
    
    def calcProperSamplingRate(resourceUtilization: Double): Double = {
        val resourceUtilizationPercentage = resourceUtilization * 100
        val base = 67.87845228
        val growth = 1.6
        math.pow(growth, ((resourceUtilizationPercentage - base) / 5))
    }

}