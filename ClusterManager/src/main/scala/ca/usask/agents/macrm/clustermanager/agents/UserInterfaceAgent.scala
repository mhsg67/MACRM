package ca.usask.agents.macrm.clustermanager.agents

import ca.usask.agents.macrm.clustermanager.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import play.api.libs.functional.syntax._
import scala.concurrent.duration._
import org.joda.time._
import play.api.libs.json._
import akka.actor._
import akka.camel._

case class InputTaskDescription(index: Int, duration: Long, relSubTime: Long, cpu: Float, memory: Float)
case class InputConstraintDescription(index:Int, value1: Int, operator: Int, value2: Int)

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
        context.system.scheduler.scheduleOnce(1000 millis, self, "sendTestJob")
    }

    def Handle_UserMessage(message: CamelMessage, sender: ActorRef) = {
        getJobDescription(message.body.toString()) match {
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

    def getJobDescription(messageBody: String): Either[String, JobDescription] = {
        try {
            val json: JsValue = Json.parse(messageBody)

            val jobId = (json \ "JI").as[Long]
            val userId = (json \ "UI").as[Int]

            implicit val tasksRead = Json.reads[InputTaskDescription]
            val tasks = (json \ "TS").as[List[InputTaskDescription]]
            
            
            
            implicit val constraintsRead = Json.reads[InputConstraintDescription]
            val constraints = (json \"CS").as[List[InputConstraintDescription]]            
            
            var temp = constraints.groupBy { x => x.index }

            //implicit val tasksRead = Json.reads[testTask]
            //val tasks = (json \ "tasks").as[List[testTask]]            

            //tasks.foreach { x => println(x.index) }

            //println(jobId.toString())

            return Left(temp.toString())
        }
        catch {
            case e: Exception => Left(e.getMessage)
        }
    }
}