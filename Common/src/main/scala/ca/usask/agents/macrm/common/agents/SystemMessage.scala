package ca.usask.agents.macrm.common.agents

import akka.actor._
import scala.collection._
import scala.collection.mutable.ArrayBuffer
import org.joda.time.DateTime

import ca.usask.agents.macrm.common.records._

trait BasicMessage

/**
 * These are basic message that each agent (RM,RT,NM,JB) use internally 
 */
case class _Resource(val _resource: Resource) extends BasicMessage



abstract class SystemMessage(val source: ActorRef, val recieveTime: DateTime)
    extends BasicMessage    
 /**
 * from ApplicationMasterAgent(AMA) to ResourceManager:QueueAgent(RMA)
 */
case class Message_ResourceRequest_AMAtoRMA(_source: ActorRef, _time: DateTime, var listOfResourceRequests: ArrayBuffer[ResourceRequest])
    extends SystemMessage(_source, _time)

/**
 * from ClientAgent(CA) to ResourceManager:QueueAgent(RMA)
 */
case class Message_ResourceRequest_CAtoRMA(_source: ActorRef, _time: DateTime, var listOfResourceRequests: ArrayBuffer[ResourceRequest])
    extends SystemMessage(_source, _time)

/**
 * from ResourceManager:...Agent(RMA) to either ClientAgent(CA) or ApplicationMasterAgent(AMA)
 */
case class Message_ResourceRequestResponse_RMAtoCAorAMA(_source: ActorRef, _time: DateTime)
    extends SystemMessage(_source, _time)

/**
 * from SchedulerAgents(SA) to QueueAgent(QA)
 */
case class Message_GiveNextResourceRequest_SAtoQA(_source: ActorRef, _time: DateTime)
    extends SystemMessage(_source, _time)

/**
 * from QueueAgent(QA) to SchedulerAgent(SA) in response to Message_GiveNextResourceRequest_SAtoQA
 *
 */
case class Message_TakeNextResourceRequest_QAtoSA(_source: ActorRef, _time: DateTime, resourceRequest: ResourceRequest)
    extends SystemMessage(_source, _time)

/**
 * from each NodeMangerAgent(NMA) to ResourceManager:ClusterStateAgent(RMA)
 */
case class Message_HeartBeat_NMAtoRMA(_source: ActorRef, _time: DateTime)
    extends SystemMessage(_source, _time)

/**
 * from each SchedulerAgent(SA) to ClusterStateAgent(CSA)
 */
case class Message_GiveClusterState_SAtoCSA(_source: ActorRef, _time: DateTime)
    extends SystemMessage(_source, _time)

/**
 * from ClusterStateAgent(CSA) to SchedulerAgent(SA) in response to Message_GiveClusterState_SAtoCSA
 */
case class Message_TakeClusterState_CSAtoSA(_source: ActorRef, _time: DateTime)
    extends SystemMessage(_source, _time)

/**
 * from ResourceManager:SchedulerAgent(RMA) to NodeManagerAgent(NMA)
 */
case class Message_ResourceRequest_RMAtoNMA(_source: ActorRef, _time: DateTime)
    extends SystemMessage(_source, _time)

/**
 * from NodeMangerAgent(NMA) to ResourceManager:SchedulerAgent(RMA) in response to Message_ResourceRequest_RMAtoNMA
 */
case class Message_ResourceRequestResponse_NMAtoRMA(_source: ActorRef, _time: DateTime)
    extends SystemMessage(_source, _time)

/**
 * from ApplicationMasterAgent(AMA) to NodeManagerAgent(NMA)
 */
case class Message_ResourceRequest_AMAtoNMA(_source: ActorRef, _time: DateTime)
    extends SystemMessage(_source, _time)

/**
 * from NodeManagerAgent(NMA) to ApplicationMasterAgent(AMA) in response to Message_ResourceRequest_AMAtoNMA
 */
case class Message_ResourceRequestResponse_NMAtoAMA(_source: ActorRef, _time: DateTime)
    extends SystemMessage(_source, _time)
