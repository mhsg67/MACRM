package ca.usask.agents.macrm.clustermanager.agents

import ca.usask.agents.macrm.clustermanager.utils._
import ca.usask.agents.macrm.common.agents._
import com.typesafe.config.ConfigFactory
import akka.actor._
import ca.usask.agents.macrm.common.records.TaskDescription
import ca.usask.agents.macrm.common.records.JobDescription
import ca.usask.agents.macrm.common.agents._AllocateContainerForJobManager
import org.joda.time.DateTime

/**
 * TODO: For adaption part which switch to central decision making
 * create some scheduler actors and forward resource request for allocation to them
 */
class QueueAgent extends Agent {

    val schedulingQueue = AbstractQueue(ClusterManagerConfig.QueueType)

    val system = ActorSystem.create("SchedulerAgent", ConfigFactory.load().getConfig("SchedulerAgent"))

    def receive = {
        case "initiateEvent"                    => Event_initiate()
        case "getNextTaskForScheduling"         => Handle_getNextTaskForScheduling(sender)
        case message: _ServerWithEmptyResources => Handle_ServerWithEmptyResources(message)
        case message: _EachUserShareOfCluster   => Handle_EachUserShareOfCluster(message)
        case message: _JobSubmission            => Handle_JobSubmission(message)
        case _                                  => Handle_UnknownMessage
        //TODO:Implement it //case message: _TaskSubmission           => Hande_TaskSubmission(message)
    }

    def Event_initiate() = {
        Logger.Log("QueueAgent Initialization")
    }

    def Handle_getNextTaskForScheduling(sender: ActorRef) = {

    }

    def Handle_ServerWithEmptyResources(message: _ServerWithEmptyResources) = {
        //TODO: check if you can make it parallel the headOfTaskQueue and 
        //the headOfJobQueue fetching, be careful about actor system 
        val headOfTaskQueue = schedulingQueue.getFirtOrBestMatchTask(message._report.capability, message._report.otherCapablity)
        if (headOfTaskQueue != None) {
            schedulingQueue.RemoveTask(headOfTaskQueue.get._2)
            scheduleTask(headOfTaskQueue.get, message)
        }
        else {
            val headOfJobQueue = schedulingQueue.getFirstOrBestMatchJob(message._report.capability, message._report.otherCapablity)
            if (headOfJobQueue != None) {
                schedulingQueue.RemoveJob(headOfJobQueue.get)
                schedulerJob(headOfJobQueue.get, message)
            }
        }
    }

    def Handle_EachUserShareOfCluster(message: _EachUserShareOfCluster) = {

    }

    def Handle_JobSubmission(message: _JobSubmission) = schedulingQueue.EnqueueJob(message.jobDescription)

    def scheduleTask(task: Tuple2[ActorRef, TaskDescription], message: _ServerWithEmptyResources) = null

    def schedulerJob(job: JobDescription, message: _ServerWithEmptyResources) = message._report.nodeId.agent ! new _AllocateContainerForJobManager(self, DateTime.now(), job)
}
