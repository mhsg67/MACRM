package ca.usask.agents.macrm.common.agents

import akka.actor._
import org.joda.time._
import scala.collection._
import scala.collection.mutable.ArrayBuffer
import ca.usask.agents.macrm.common.records._

case class _Resource(val _resource: Resource) extends BasicMessage

case class _ContainerExecutionFinished(val containerId: Long, val isJobManager: Boolean) extends BasicMessage

case class _AllocateContainer(val userId: Int, val jobId: Long, val taskIndex: Int, val size: Resource, val duration: Duration = null, val isHeartBeatRespond: Boolean = false) extends BasicMessage

case class _JobSubmission(val jobDescription: JobDescription) extends BasicMessage

case class _TaskSubmission(val taskDescriptions: List[TaskDescription]) extends BasicMessage

case class _NodeSamplingTimeout(val forWave: Int, val retry: Int) extends BasicMessage

case class _NodeManagerSimulationInitiate(val resource: Resource, val initialLoad:Resource, val capabilities: List[Constraint])

case class _JobManagerSimulationInitiate(val taskDescriptions: List[TaskDescription]) extends BasicMessage

case class _headOfSchedulingQueue(val jobOrTask: Either[JobDescription, TaskDescription]) extends BasicMessage

case class _NodesWithFreeResources(val nodes: List[(NodeId, Resource)]) extends BasicMessage

case class _UpdateNodesWithFreeResourcesTransaction(val nodes: List[(NodeId, Resource)]) extends BasicMessage

case class _NodeWithFreeResourcesIsInconsistance(val nodes: List[(NodeId, Resource)]) extends BasicMessage

case class _UnsuccessfulPartOfTrasaction(val nodes: List[NodeId]) extends BasicMessage

case class _ridi(val resource: Resource) extends BasicMessage