package ca.usask.agents.macrm.common.agents

import org.joda.time.DateTime
import akka.actor._
import ca.usask.agents.macrm.common.records.JobDescription

case class _AllocateContainerForJobManager(_source: ActorRef, _time: DateTime, _jobDescription: JobDescription)
    extends SystemMessage(_source, _time)