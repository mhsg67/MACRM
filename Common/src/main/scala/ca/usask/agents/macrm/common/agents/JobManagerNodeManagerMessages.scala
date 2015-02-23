package ca.usask.agents.macrm.common.agents

import ca.usask.agents.macrm.common.records.NodeReport
import org.joda.time.DateTime
import akka.actor._
import ca.usask.agents.macrm.common.records.Resource

/**
 * From JobManager to NodeManager to inquiry about node resource
 * availability
 */
case class _ResourceSamplingInquiry(_source: ActorRef, _time: DateTime, _requiredResource: Resource)
    extends BasicMessage

/**
 * From JobManager to NodeManger to cancel previous resource inquiry
 * request
 */
case class _ResourceSamplingCancel(_source: ActorRef, _time: DateTime)
    extends BasicMessage

/**
 * From NodeManager to JobManager as a response for resource availability
 * inquiry (_ResourceSamplingInquiry)
 */
case class _ResourceSamplingResponse(_source: ActorRef, _time: DateTime, _availableResource: Resource)
    extends BasicMessage

