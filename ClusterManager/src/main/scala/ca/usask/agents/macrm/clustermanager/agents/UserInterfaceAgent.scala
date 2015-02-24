package ca.usask.agents.macrm.clustermanager.agents

import ca.usask.agents.macrm.clustermanager.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import org.joda.time._
import akka.actor._
import akka.camel._

class UserInterfaceAgent(val queueAgent: ActorRef) extends Agent with Consumer {

    var jobIdToUserRef = scala.collection.mutable.Map[Long, ActorRef]()

    def endpointUri = "netty:tcp://0.0.0.0:2001?textline=true"

    def receive = {
        case "initiateEvent"       => Event_initiate()
        case message: CamelMessage => Handle_UserMessage(message, sender())
        case "sendTestJob"         => TESTsendTestJob()
        case _                     => Handle_UnknownMessage
    }

    import context.dispatcher

    def Event_initiate() = {
        Logger.Log("UserInterfaceAgent Initialization")
        //context.system.scheduler.scheduleOnce(1000 millis, self, "sendTestJob")
    }

    def Handle_UserMessage(message: CamelMessage, sender: ActorRef) = {
        JSONManager.getJobDescription(message.body.toString()) match {
            case Left(x) => sender ! "Incorrect job submission format: " + x
            case Right(x) => {
                jobIdToUserRef(x.jobId) = sender
                queueAgent ! x
            }
        }

    }

    val task1 = new TaskDescription(null, 1, 0, new org.joda.time.Duration(100), new Resource(1, 250), new org.joda.time.Duration(2), null)
    val task2 = new TaskDescription(null, 1, 1, new org.joda.time.Duration(200), new Resource(1, 250), new org.joda.time.Duration(2), null)
    val job1 = new JobDescription(1, 1, 2, List(task1, task2), null)

    def TESTsendTestJob() = {
        queueAgent ! new _JobSubmission(job1)
    }
    
}