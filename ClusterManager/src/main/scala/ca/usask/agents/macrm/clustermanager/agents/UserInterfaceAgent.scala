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

    var submittedJobCache: String = ""
    var jobIdToJobSubmission = Map[Long, Long]()
    def endpointUri = "netty:tcp://" + ClusterManagerConfig.clusterManagerIpAddress + ":2001?textline=true"
    
    def receive = {
        case "initiateEvent"       => Event_initiate()
        case message: _JobFinished => Handle_JobFinished(message)
        case message: CamelMessage => Handle_UserMessage(message, sender())
        case _                     => Handle_UnknownMessage("UserInterfaceAgent")
    }

    def Event_initiate() = {
        Logger.Log("UserInterfaceAgent Initialization")
        Logger.Log("UserInterfaceAgent Initialization End")
    }

    def Handle_UserMessage(message: CamelMessage, sender: ActorRef) = {
        println(message.body.toString())
        message.body.toString() match {
            case "$$$" => JSONManager.getJobDescription(submittedJobCache) match {
                case Left(x) => {
                    sender ! "Incorrect job submission format: " + x
                    submittedJobCache = ""
                }
                case Right(x) => {
                    val response = "received:" + x.jobId.toString
                    sender ! response

                    jobIdToJobSubmission.update(x.jobId, DateTime.now().getMillis)
                    queueAgent ! new _JobSubmission(x)
                    submittedJobCache = ""
                }
            }
            case _ => {
                submittedJobCache = submittedJobCache + message.body.toString()
            }
        }
    }

    def Handle_JobFinished(message: _JobFinished) = {
        if (message._jobId < 100000)
            println(message._jobId + "\t" + (DateTime.now().getMillis - jobIdToJobSubmission.get(message._jobId).get).toString())
    }
}