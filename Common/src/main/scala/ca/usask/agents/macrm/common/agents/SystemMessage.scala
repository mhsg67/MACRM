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

case class _FindResources(val _requirment:List[(Resource,NodeConstraint,Int)]) extends BasicMessage

case class _JobSubmission(val jobDescription:JobDescription)

class SystemMessage(val source: ActorRef, val recieveTime: DateTime)
    extends BasicMessage    
      




/**
 * From JobManager to ResourceTracker to inform it about job
 * current condition
 */
case class _JMHeartBeat(_source: ActorRef, _time: DateTime, _jobReport: JobReport)
    extends SystemMessage(_source, _time)

/**
 * From ResourceTracker to JobManager to inform it about
 * failure in one of its container
 *
 * TODO: It can be used for both node failure and
 * container failure, then add required information into
 * the message such as node information and container
 * information
 */
case class _ContainerFailure(_source: ActorRef, _time: DateTime)
    extends SystemMessage(_source, _time)

/**
 * From ResourceTracker to JobManager
 */
case class _JMHeartBeatResponse(_source: ActorRef, _time: DateTime, _samplingRate:Int)


/**
 * From JobManager to NodeManager to inquiry about node resource
 * availability
 */
case class _ResourceSamplingInquiry(_source: ActorRef, _time: DateTime, _requiredResource: Resource)
    extends SystemMessage(_source, _time)

/**
 * From JobManager to NodeManger to cancel previous resource inquiry
 * request
 */
case class _ResourceSamplingCancel(_source: ActorRef, _time: DateTime)
    extends SystemMessage(_source, _time)

/**
 * From NodeManager to JobManager as a response for resource availability 
 * inquiry (_ResourceSamplingInquiry)
 */
case class _ResourceSamplingResponse(_source: ActorRef, _time: DateTime, _availableResource: Resource)
    extends SystemMessage(_source, _time)