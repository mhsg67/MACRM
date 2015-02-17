package ca.usask.agents.macrm.clustermanager.agents

import ca.usask.agents.macrm.clustermanager.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import play.api.libs.json._
import akka.actor._
import akka.camel._
import ca.usask.agents.macrm.common.records.JobDescription

class UserInterfaceAgent(val queueAgent: ActorRef) extends Agent with Consumer {

    var jobIdToUserRef = scala.collection.mutable.Map[Int,ActorRef]()
    
    def endpointUri = "netty:tcp://0.0.0.0:3001?textline=true"

    def receive = {
        case "initiateEvent"       => Event_initiate()
        case message: CamelMessage => Handle_UserMessage(message, sender())
        case _                     => Handle_UnknownMessage

    }

    def Event_initiate() = {
        Logger.Log("UserInterfaceAgent Initialization")
    }

    def Handle_UserMessage(message: CamelMessage, sender: ActorRef) = {
        getJobDescription(message.body.toString()) match {            
            case Left(x)  => sender ! "Incorrect job submission format: " + x
            case Right(x) => {
              jobIdToUserRef(x.jobId) = sender
              queueAgent ! x
            } 
        }

    }

    def getJobDescription(messageBody: String): Either[String, JobDescription] = {
        try {
            val json: JsValue = Json.parse(messageBody)
            return Right(null)
        }
        catch {
            case e: Exception => Left(e.getMessage)
        }
    }
}