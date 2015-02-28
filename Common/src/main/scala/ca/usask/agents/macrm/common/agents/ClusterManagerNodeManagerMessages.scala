package ca.usask.agents.macrm.common.agents

import ca.usask.agents.macrm.common.records._
import org.joda.time.DateTime
import akka.actor._

/**
 * From clusterManager(either queueAgent or SchedulingAgent) For allocating a
 * container for executing a JobManager for a submitted job
 */
case class _AllocateContainerForJobManager(_source: ActorRef, _time: DateTime, _jobDescription: JobDescription, _samplingInformation: SamplingInformation)
    extends BasicMessage

/**
 * From clusterManager(either queueAgent or SchedulingAgent) For allocating a
 * container for executing a tasks for submitted Task
 */
case class _AllocateContainerForTask(_source: ActorRef, _time: DateTime, _taskDescription: List[TaskDescription])
    extends BasicMessage

/**
 * TODO: replace this message with the two above
 */
case class _AllocateContainerFromCM(_source: ActorRef, _time: DateTime, _taskDescriptions: List[TaskDescription], _jobDescriptions: List[(JobDescription, SamplingInformation)])
    extends BasicMessage