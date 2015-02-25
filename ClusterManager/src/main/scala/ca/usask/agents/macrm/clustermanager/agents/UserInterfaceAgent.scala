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
        case _                     => Handle_UnknownMessage
    }

    import context.dispatcher

    def Event_initiate() = {
        Logger.Log("UserInterfaceAgent Initialization")
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
}