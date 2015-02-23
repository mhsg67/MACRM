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

case class _TaskSubmission(val taskDescription:TaskDescription) extends BasicMessage 