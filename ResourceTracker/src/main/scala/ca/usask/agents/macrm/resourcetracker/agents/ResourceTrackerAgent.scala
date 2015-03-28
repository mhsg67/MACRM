package ca.usask.agents.macrm.resourcetracker.agents

import ca.usask.agents.macrm.resourcetracker.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import org.joda.time._
import akka.actor._
import java.util.Formatter.DateTime

class ResourceTrackerAgent extends Agent {

    var currentSamplingRate = 2
    var hasSubmittedFirstClusterState = false
    val clusterManagerAgent = context.actorSelection(ResourceTrakerConfig.getClusterManagerAddress())

    import context.dispatcher
    override def preStart() = {
        context.system.scheduler.scheduleOnce(ResourceTrakerConfig.firstClusterStateUpdateDelay, self, "sendFirstClusterStateUpdate")
    }

    def receive = {
        case "initiateEvent"                => Event_initiate()
        case "finishedCentralizeScheduling" => Handle_FinishedCentralizeScheduling()
        case "sendFirstClusterStateUpdate"  => Event_sendFirstClusterStateUpdate()
        case message: _HeartBeat            => Handle_HeartBeat(sender(), message)
        case message: _JMHeartBeat          => Handle_JMHeartBeat(message)
        case _                              => Handle_UnknownMessage("ResourceTrackerAgent")
    }

    def Event_initiate() = {
        Logger.Log("ResourceTrackerAgent Initialization")
    }

    def Handle_FinishedCentralizeScheduling() = {
        //TODO: Stop sending _ServerStatusUpdate
        //TODO: Start sending _ServerWithEmptyResources
    }

    def Event_sendFirstClusterStateUpdate() = {
        hasSubmittedFirstClusterState = true
        clusterManagerAgent ! new _ClusterState(self, DateTime.now(), currentSamplingRate, null, ClusterDatabase.getNodeIdToContaintsMaping(), null)
    }

    def Handle_HeartBeat(sender: ActorRef, message: _HeartBeat) = {
        updateClusterDatebaseByNMHeartBeat(sender, message)

        if (doesServerHaveResourceForAJobManager(message._report) && hasSubmittedFirstClusterState)
            clusterManagerAgent ! new _ServerWithEmptyResources(self, DateTime.now(), addIPandPortToNodeReport(message._report, sender))
        else
            sender ! "emptyHeartBeatResponse"
    }

    def updateClusterDatebaseByNMHeartBeat(sender: ActorRef, message: _HeartBeat) = {
        val nodeId = new NodeId(sender.path.address.host.get, sender.path.address.port.get, message._source)
        val usedResources = message._report.containers.foldLeft(new Resource(0, 0))((x, y) => y.resource + x)
        ClusterDatabase.updateNodeState(nodeId, message._report.resource, usedResources, message._report.capabilities,
            message._report.utilization, message._report.queueState)
        ClusterDatabase.updateNodeContainer(nodeId, message._report.containers)
    }

    def doesServerHaveResourceForAJobManager(_nodeReport: NodeReport) = if (_nodeReport.getFreeResources() > ResourceTrakerConfig.minResourceForJobManager) true else false

    def addIPandPortToNodeReport(oldReport: NodeReport, sender: ActorRef) =
        new NodeReport(new NodeId(sender.path.address.host.get, sender.path.address.port.get, sender),
            oldReport.resource, oldReport.capabilities, oldReport.containers, oldReport.utilization,
            oldReport.nodeState, oldReport.queueState)

    def Handle_JMHeartBeat(message: _JMHeartBeat) = {
        val currentUtilization = ClusterDatabase.getCurrentClusterLoad()
        val maxResourceUtilization =
            if (currentUtilization.memoryUtilization > currentUtilization.virtualCoreUtilization)
                currentUtilization.memoryUtilization
            else
                currentUtilization.virtualCoreUtilization

        val properSamplingRate = ((math.log(0.05) / math.log(maxResourceUtilization)) + 0.5).toInt

        if (properSamplingRate != currentSamplingRate && properSamplingRate >= 2) {
            currentSamplingRate = properSamplingRate
            clusterManagerAgent ! new _ClusterState(self, DateTime.now(), currentSamplingRate, null, null, null)
        }

        println(currentUtilization)
    }
}