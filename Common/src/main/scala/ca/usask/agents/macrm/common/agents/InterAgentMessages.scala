package ca.usask.agents.macrm.common.agents

import ca.usask.agents.macrm.common.records._
import org.joda.time.DateTime
import akka.actor._

/**
 * From clusterManager(either queueAgent or SchedulerAgent) to nodeManager For allocating a container in distributed Mode
 */
case class _AllocateContainerFromCM(_source: ActorRef, _time: DateTime, _taskDescriptions: List[TaskDescription], _jobDescriptions: List[(JobDescription, SamplingInformation)], _switchToCentralizeMode: Boolean) extends BasicMessage

/**
 * From clusterManager(either queueAgent or SchedulerAgent) to nodeManager For allocating a container in centralized Mode
 */
case class _AllocateContainerFromSA(_source: ActorRef, _time: DateTime, _taskDescriptions: List[TaskDescription], _jobDescriptions: List[(JobDescription, SamplingInformation)], _centralizeMode: Int) extends BasicMessage

/**
 * From JobManager to ClusterManger for those tasks that JobManager could not
 * find resources for them by sampling or the system is in centralized scheduling
 * mode then all the JobManager should send the resource requests to ClusterManager
 */
case class _TaskSubmissionFromJM(_source: ActorRef, _time: DateTime, _taskDescriptions: List[TaskDescription]) extends BasicMessage

/**
 * From JobManager to ClusterManager to inform it about completion of job
 */
case class _JobFinished(_source: ActorRef, _time: DateTime, _jobId: Long) extends BasicMessage

/**
 * From JobManager to NodeManager to inquiry about node resource availability
 */
case class _ResourceSamplingInquiry(_source: ActorRef, _time: DateTime, _minRequiredResource: Resource, _jobId: Long) extends BasicMessage

/**
 * From JobManager to NodeManger to cancel previous resource inquiry request
 */
case class _ResourceSamplingCancel(_source: ActorRef, _time: DateTime, _jobId: Long) extends BasicMessage

/**
 * From NodeManager to JobManager as a response for resource availability inquiry (_ResourceSamplingInquiry)
 */
case class _ResourceSamplingResponse(_source: ActorRef, _time: DateTime, _availableResource: Resource) extends BasicMessage

/**
 * From JobManager to NodeManager in response to _ResourceSamplingResponse for Allocating container for provided list of tasks
 */
case class _AllocateContainerFromJM(_source: ActorRef, _time: DateTime, _taskDescriptions: List[TaskDescription]) extends BasicMessage

/**
 * From NodeManager to JobManager to inform it that the a specific task execution finished
 */
case class _TasksExecutionFinished(_source: ActorRef, _time: DateTime, _taskIndex: Int) extends BasicMessage

/**
 * From JobManager to ResourceTracker to inform it about job current condition
 */
case class _JMHeartBeat(_source: ActorRef, _time: DateTime, _jobReport: JobReport) extends BasicMessage

/**
 * From ResourceTracker to JobManager to inform it about failure in one of its container
 */
case class _ContainerFailure(_source: ActorRef, _time: DateTime) extends BasicMessage

/**
 * From ResourceTracker to JobManager to send job heartbeat
 */
case class _JMHeartBeatResponse(_source: ActorRef, _time: DateTime, _samplingRate: Int) extends BasicMessage

/**
 * From NodeManger to ResourceTacker to inform it about node current condition
 */
case class _HeartBeat(_source: ActorRef, _time: DateTime, _report: NodeReport) extends BasicMessage

/**
 * From either ClusterManager or ResourceTracker to NodeManager in response to node heartbeat
 */
case class _EmptyHeartBeatResponse(_switchToCentralizeMode: Int) extends BasicMessage

/**
 * From ResourceTracker to ClusterManager to inform it about changes in cluster structure and sampling rates
 */
case class _ClusterState(_source:ActorRef, _time: DateTime, _newSamplingRate:Double = -1.0, _removedServers:List[NodeId] = null, _addedServers:List[(NodeId,List[Constraint])] = null, _rareResources:List[(Boolean,Constraint)] = null, _switchToDistributedMode:Boolean = true)

/**
 * From ResourceTracker to ClusterManager to inform it about a node with free resources. 
 */
case class _ServerWithEmptyResources(_source: ActorRef, _time: DateTime, _report: NodeReport) extends BasicMessage

/**
 * From ResourceTracker to ClusterManager to inform it about Sharing of cluster among users.
 * TODO: Add information of user share into the message
 */
case class _EachUserShareOfCluster(_source: ActorRef, _time: DateTime) extends BasicMessage