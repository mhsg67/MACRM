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

    def receive = {
        case "initiateEvent"                    => Event_initiate()
        case "getNextTaskForScheduling"         => Handle_getNextTaskForScheduling(sender)
        case message: _ServerWithEmptyResources => Handle_ServerWithEmptyResources(message)
        case message: _EachUserShareOfCluster   => Handle_EachUserShareOfCluster(message)
        case message: _JobSubmission            => Handle_JobSubmission(message)
        case message: _TaskSubmission           => Handle_TaskSubmission(message)
        case _                                  => Handle_UnknownMessage
    }

    def Event_initiate() = {
        Logger.Log("QueueAgent Initialization")
    }

    //TODO: check if you can make it parallel the headOfTaskQueue and 
    //the headOfJobQueue fetching, be careful about actor system 
    def Handle_ServerWithEmptyResources(message: _ServerWithEmptyResources) = {
        val headOfTaskQueue = schedulingQueue.getFirtOrBestMatchTask(message._report.capability, message._report.otherCapablity)
        if (!headOfTaskQueue.isEmpty) {
            println("**** Schedule a Task")
            schedulingQueue.RemoveTask(headOfTaskQueue.get)
            scheduleTask(headOfTaskQueue.get, message)
        }
        else {
            val headOfJobQueue = schedulingQueue.getFirstOrBestMatchJob(message._report.capability, message._report.otherCapablity)
            if (!headOfJobQueue.isEmpty) {
                println("**** Schedule a Job")
                schedulingQueue.RemoveJob(headOfJobQueue.get)
                if(headOfJobQueue.get.numberOfTasks != 1)
                    schedulerJob(headOfJobQueue.get, message)
                else
                    schedulerJob(headOfJobQueue.get, message)
            }
        }
    }

    def Handle_JobSubmission(message: _JobSubmission) = schedulingQueue.EnqueueJob(message.jobDescription)

    def Handle_TaskSubmission(message: _TaskSubmission) = schedulingQueue.EnqueueTask(message.taskDescription)

    def scheduleTask(task: TaskDescription, message: _ServerWithEmptyResources) = message._report.nodeId.agent ! new _AllocateContainerForTask(self, DateTime.now, task)

    def schedulerJob(job: JobDescription, message: _ServerWithEmptyResources) =  message._report.nodeId.agent ! new _AllocateContainerForJobManager(self, DateTime.now(), job)

    /*
     * TODO: Implement following functions
     */

    def Handle_getNextTaskForScheduling(sender: ActorRef) = {

    }

    def Handle_EachUserShareOfCluster(message: _EachUserShareOfCluster) = {

    }
}
