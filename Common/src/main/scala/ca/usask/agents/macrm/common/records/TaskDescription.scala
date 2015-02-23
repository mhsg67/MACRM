package ca.usask.agents.macrm.common.records

import ca.usask.agents.macrm.common.records._
import org.joda.time._
import java.io.Serializable
import akka.actor._

@SerialVersionUID(100L)
case class TaskDescription(val jobManagerRef: ActorRef,
                           val jobId: Long,
                           val index: Int,
                           val duration: Duration,
                           val resource: Resource,
                           val relativeSubmissionTime: Duration,
                           val constraints: List[Int]) extends Serializable