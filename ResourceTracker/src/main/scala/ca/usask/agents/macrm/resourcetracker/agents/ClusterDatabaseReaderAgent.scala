package ca.usask.agents.macrm.resourcetracker.agents

import ca.usask.agents.macrm.resourcetracker.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import org.joda.time._
import akka.actor._
import java.util.Formatter.DateTime

class ClusterDatabaseReaderAgent(val resourceTrackerAgent: ActorRef) extends Agent {

    var lastClusterUtilizationLevel = new Utilization(0.0, 0.0)
    var currentSamplingRate = 2

    import context.dispatcher
    override def preStart() = {
        context.system.scheduler.scheduleOnce(ResourceTrakerConfig.firstClusterStateUpdateDelay, self, "sendFirstClusterStateUpdate")
    }

    def receive = {
        case "initiateEvent"               => Event_initiate()
        case "sendFirstClusterStateUpdate" => Event_sendFirstClusterStateUpdate()
        case message: _JMHeartBeat         => Handle_JMHeartBeat(message)
        case _                             => Handle_UnknownMessage
    }

    def Event_sendFirstClusterStateUpdate() = {
        resourceTrackerAgent ! new _ClusterState(resourceTrackerAgent, DateTime.now(), currentSamplingRate, null, ClusterDatabase.getNodeIdToContaintsMaping(), null)
    }

    def Handle_JMHeartBeat(message: _JMHeartBeat) = {

        val currentUtilization = ClusterDatabase.getCurrentClusterLoad()
        val maxResourceUtilization =
            if (currentUtilization.memoryUtilization > currentUtilization.virtualCoreUtilization)
                currentUtilization.memoryUtilization
            else
                currentUtilization.virtualCoreUtilization
        
       val properSamplingRate = ((math.log(0.05)/math.log(maxResourceUtilization)) + 0.5).toInt
       
       if(properSamplingRate != currentSamplingRate && properSamplingRate >= 2){
           currentSamplingRate = properSamplingRate
           resourceTrackerAgent ! new _ClusterState(resourceTrackerAgent, DateTime.now(), currentSamplingRate, null, null, null)
       }
    }

    def Event_initiate() = {
        Logger.Log("ClusterDatabaseReaderAgent Initialization")
    }

}