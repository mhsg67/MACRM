package ca.usask.agents.macrm.resourcetracker.agents

import ca.usask.agents.macrm.resourcetracker.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import org.joda.time._
import akka.actor._
import java.util.Formatter.DateTime

class ResourceTrackerAgent extends Agent {

    var currentSamplingRate = 2.0
    var isInCentralizeState = 0
    val clusterManagerAgent = context.actorSelection(ResourceTrakerConfig.getClusterManagerAddress())

    def addIPandPortToNodeReport(oldReport: NodeReport, sender: ActorRef) =
        new NodeReport(new NodeId(sender.path.address.host.get, sender.path.address.port.get, sender),
            oldReport.resource, oldReport.capabilities, oldReport.containers, oldReport.utilization,
            oldReport.nodeState, oldReport.queueState)

    def receive = {
        case "initiateEvent"       => Event_initiate()
        case message: _HeartBeat   => Handle_HeartBeat(sender(), message)
        case message: _JMHeartBeat => Handle_JMHeartBeat(message)
        case message               => Handle_UnknownMessage("ResourceTrackerAgent", message)
    }

    def Event_initiate() = Logger.Log("ResourceTrackerAgent Initialization")

    //TODO: for real test and the case of centralize scheduling you should check 
    //if the node has resource for minimum container instead of a job manager container
    def Handle_HeartBeat(sender: ActorRef, message: _HeartBeat) = {
        updateClusterDatebaseByNMHeartBeat(sender, message)
        updateClusterModeAndSamplingRate()

        if (doesServerHaveResourceForAJobManager(message._report))
            clusterManagerAgent ! new _ServerWithEmptyResources(self, DateTime.now(), addIPandPortToNodeReport(message._report, sender))
        else
            sender ! new _EmptyHeartBeatResponse(isInCentralizeState)
    }

    def updateClusterDatebaseByNMHeartBeat(sender: ActorRef, message: _HeartBeat) = {
        val nodeId = new NodeId(sender.path.address.host.get, sender.path.address.port.get, message._source)
        val usedResources = message._report.containers.foldLeft(new Resource(0, 0))((x, y) => y.resource + x)
        val isNewNode = ClusterDatabase.updateNodeState(nodeId, message._report.resource, usedResources, message._report.capabilities,
            message._report.utilization, message._report.queueState)
        ClusterDatabase.updateNodeContainer(nodeId, message._report.containers)

        if (isNewNode == true)
            clusterManagerAgent ! new _ClusterState(self, DateTime.now(), currentSamplingRate, null, List((nodeId, message._report.capabilities)), null, true)
    }

    def updateClusterModeAndSamplingRate() {
        val currentUtilization = ClusterDatabase.getCurrentClusterLoad()
        val maxResourceUtilization = if (currentUtilization.memoryUtilization > currentUtilization.virtualCoreUtilization)
            currentUtilization.memoryUtilization
        else
            currentUtilization.virtualCoreUtilization

        /*if (maxResourceUtilization >= 0.90) {
            if (isInCentralizeState == 0) {
                clusterManagerAgent ! "changeToCentralizedMode1"
                isInCentralizeState = 1
                currentSamplingRate = 2
            }
        }
        else*/ {
            if (maxResourceUtilization < 0.90 && isInCentralizeState > 0) isInCentralizeState = 0
            val properSamplingRate = calcProperSamplingRate(maxResourceUtilization)
            if (properSamplingRate != currentSamplingRate && properSamplingRate >= 2.0) {
                println("properSamplingRate " + properSamplingRate)
                currentSamplingRate = properSamplingRate
                clusterManagerAgent ! new _ClusterState(self, DateTime.now(), currentSamplingRate, null, null, null, true)
            }
        }

    }

    def doesServerHaveResourceForAJobManager(_nodeReport: NodeReport) = {
        val nodeFreeResources = _nodeReport.getFreeResources()
        if (nodeFreeResources.memory >= ResourceTrakerConfig.minResourceForJobManager.memory &&
            nodeFreeResources.virtualCore >= ResourceTrakerConfig.minResourceForJobManager.virtualCore)
            true
        else
            false
    }

    def Handle_JMHeartBeat(message: _JMHeartBeat) = {
        val currentUtilization = ClusterDatabase.getCurrentClusterLoad()
        println(currentUtilization)
        println("<sampRate:" + currentSamplingRate + ">")
    }

    def calcProperSamplingRate(resourceUtilization: Double): Double = {
        val resourceUtilizationPercentage = resourceUtilization * 100
        if (resourceUtilizationPercentage >= 75)
            (resourceUtilizationPercentage * resourceUtilizationPercentage * 0.02) - (2.9 * resourceUtilizationPercentage) + 107
        else
            2.0
    }
}