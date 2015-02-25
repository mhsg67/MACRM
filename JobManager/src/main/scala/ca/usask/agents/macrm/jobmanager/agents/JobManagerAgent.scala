package ca.usask.agents.macrm.jobmanager.agents

import ca.usask.agents.macrm.jobmanager.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import scala.collection.immutable.List
import org.joda.time.DateTime
import scala.collection._
import akka.actor._

class JobManagerAgent(val userId: Int,
                      val jobId: String,
                      val samplingInformation: SamplingInformation,
                      val jobDescription: JobDescription)
    extends Agent {

    val samplingManager = new SamplingManager()
    val resourceTracker = context.actorSelection(JobManagerConfig.getResourceTrackerAddress())

    import context.dispatcher
    override def preStart() = {
        context.system.scheduler.scheduleOnce(JobManagerConfig.heartBeatStartDelay, self, "heartBeatEvent")
    }

    def receive = {
        case "initiateEvent"               => Event_initiate()
        case "heartBeatEvent"              => Event_heartBeat()
        case message: _FindResources       => Handle_FindResources(message)
        case message: _JMHeartBeatResponse => Handle_JMHeartBeatResponse(message)
        case _                             => Handle_UnknownMessage
    }

    def Event_initiate() = {
        Logger.Log("JobManagerAgent Initialization")
    }

    def Event_heartBeat() = {
        resourceTracker ! create_JMHeartBeat()
        context.system.scheduler.scheduleOnce(JobManagerConfig.heartBeatInterval, self, "heartBeatEvent")
    }

    def create_JMHeartBeat() = new _JMHeartBeat(self, DateTime.now(), new JobReport(userId, jobId))

    def Handle_FindResources(message: _FindResources) = createSamplingList(message.tasksDescriptions, false).foreach(x => (getActorRefFromNodeId(x._1) ! _ResourceSamplingInquiry(self,DateTime.now(), x._2))) 

    def createSamplingList(tasks: List[TaskDescription], retry:Boolean): List[(NodeId, Resource)] = samplingManager.getSamplingNode(tasks,retry) 

    def getActorRefFromNodeId(node: NodeId): ActorSelection = context.actorSelection(createNodeManagerAddressString(node.host, node.port))

    def createNodeManagerAddressString(host: String, port: Int) = "akka.tcp://NodeManagerAgent@" +
        host + ":" +
        port.toString() + "/" +
        "user/NodeManagerAgent"

    def Handle_JMHeartBeatResponse(message: _JMHeartBeatResponse) = samplingManager.loadNewSamplingRate(message._samplingRate)

}
