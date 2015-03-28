package ca.usask.agents.macrm.clustermanager.agents

import ca.usask.agents.macrm.clustermanager.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import scala.collection.mutable._
import scala.concurrent.duration._
import org.joda.time._
import akka.actor._
import akka.camel._

class UserInterfaceAgent(val queueAgent: ActorRef) extends Agent with Consumer {

    var jobIdToUserRef = Map[Long, ActorRef]()
    var jobIdToJobSubmission = Map[Long, (DateTime,DateTime)]()
    var numberOfSubmittedJob = 0

    def endpointUri = "netty:tcp://127.0.0.1:2001?textline=true"

    def receive = {
        case "initiateEvent"            => Event_initiate()
        case message: _JobFinished      => Handle_JobFinished(message)
        case message: CamelMessage      => Handle_UserMessage(message, sender())
        case _                          => Handle_UnknownMessage("UserInterfaceAgent")
    }

    def Event_initiate() = {
        Logger.Log("UserInterfaceAgent Initialization")
        Logger.Log("UserInterfaceAgent Initialization End")
    }


    def Handle_UserMessage(message: CamelMessage, sender: ActorRef) = {
        JSONManager.getJobDescription(message.body.toString()) match {
            case Left(x) => sender ! "Incorrect job submission format: " + x
            case Right(x) => {
                val response = "received:" + x.jobId.toString
                sender ! response
                jobIdToUserRef.update(x.jobId, sender)
                queueAgent ! new _JobSubmission(x)
            }
        }
    }

    def Handle_JobFinished(message: _JobFinished) = {
        val response = "finished:" + message._jobId
        jobIdToUserRef(message._jobId) ! response
    }
}