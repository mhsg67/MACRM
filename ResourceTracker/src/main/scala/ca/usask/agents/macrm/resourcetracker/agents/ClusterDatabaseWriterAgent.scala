package ca.usask.agents.macrm.resourcetracker.agents

import ca.usask.agents.macrm.resourcetracker.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import org.joda.time._
import akka.actor._

class ClusterDatabaseWriterAgent(val resourceTrackerAgent: ActorRef) extends Agent {

    var currentSamplingRate = 2.0
    var isInCentralizeState = false

    def receive = {
        case "initiateEvent"     => Event_initiate()
        case message: _HeartBeat => Handle_HeartBeat(message)
        case message             => Handle_UnknownMessage("ClusterDatabaseWriterAgent", message)
    }

    def Event_initiate() = 
        Logger.Log("ClusterDatabaseWriterAgent Initialization")

    def Handle_HeartBeat(message: _HeartBeat) = {
        val nodeId = new NodeId(sender.path.address.host.get, sender.path.address.port.get, message._source)
        val usedResources = message._report.containers.foldLeft(new Resource(0, 0))((x, y) => y.resource + x)
        val isNewNode = ClusterDatabase.updateNodeState(nodeId, message._report.resource, usedResources, message._report.capabilities,
            message._report.utilization, message._report.queueState)
        ClusterDatabase.updateNodeContainer(nodeId, message._report.containers)

        if (isNewNode == true)
            resourceTrackerAgent ! new _ClusterState(self, DateTime.now(), -1.0, null, List((nodeId, message._report.capabilities)), null, true)
    }

    def Handle_HeartBeat(sender: ActorRef, message: _HeartBeat) = {
        updateClusterDatebaseByNMHeartBeat(sender, message)
        updateClusterModeAndSamplingRate()
    }

    def updateClusterDatebaseByNMHeartBeat(sender: ActorRef, message: _HeartBeat) = {
        val nodeId = new NodeId(sender.path.address.host.get, sender.path.address.port.get, message._source)
        val usedResources = message._report.containers.foldLeft(new Resource(0, 0))((x, y) => y.resource + x)
        val isNewNode = ClusterDatabase.updateNodeState(nodeId, message._report.resource, usedResources, message._report.capabilities,
            message._report.utilization, message._report.queueState)
        ClusterDatabase.updateNodeContainer(nodeId, message._report.containers)

        if (isNewNode == true)
            resourceTrackerAgent ! new _ClusterState(self, DateTime.now(), currentSamplingRate, null, List((nodeId, message._report.capabilities)), null, true)
    }

    def updateClusterModeAndSamplingRate() {
        val currentUtilization = ClusterDatabase.getCurrentClusterLoad()
        val maxResourceUtilization = if (currentUtilization.memoryUtilization > currentUtilization.virtualCoreUtilization)
            currentUtilization.memoryUtilization
        else
            currentUtilization.virtualCoreUtilization

        if (maxResourceUtilization >= 0.90) {
            if (isInCentralizeState == false) {
                resourceTrackerAgent ! "changeToCentralizedMode1"
                isInCentralizeState = true
            }
        }
        else {
            if (maxResourceUtilization < 0.90 && isInCentralizeState == true) isInCentralizeState = false
            val properSamplingRate = calcProperSamplingRate(maxResourceUtilization)
            if (properSamplingRate != currentSamplingRate && properSamplingRate >= 2.0) {
                currentSamplingRate = properSamplingRate
                resourceTrackerAgent ! new _ClusterState(self, DateTime.now(), currentSamplingRate, null, null, null, true)
            }
        }

    }

    def calcProperSamplingRate(resourceUtilization: Double): Double = {
        val resourceUtilizationPercentage = resourceUtilization * 100
        if (resourceUtilizationPercentage >= 75)
            (resourceUtilizationPercentage * resourceUtilizationPercentage * 0.02) - (2.9 * resourceUtilizationPercentage) + 107
        else
            2.0
    }

}