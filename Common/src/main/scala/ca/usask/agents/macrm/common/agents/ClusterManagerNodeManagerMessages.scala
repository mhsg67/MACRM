package ca.usask.agents.macrm.common.agents

import org.joda.time.DateTime
import akka.actor._
import ca.usask.agents.macrm.common.records.JobDescription
import ca.usask.agents.macrm.common.records.TaskDescription

/**
 * From clusterManager(either queueAgent or SchedulingAgent) For allocating a
 * container for executing a JobManager for a submitted job
 */
case class _AllocateContainerForJobManager(_source: ActorRef, _time: DateTime, _jobDescription: JobDescription)
    extends BasicMessage

/**
 * From clusterManager(either queueAgent or SchedulingAgent) For allocating a
 * container for executing a tasks for submitted Task
 */
case class _AllocateContainerForTask(_source: ActorRef, _time: DateTime, _taskDescription: TaskDescription)
    extends BasicMessage