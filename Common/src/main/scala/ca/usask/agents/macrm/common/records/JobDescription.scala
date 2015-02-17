package ca.usask.agents.macrm.common.records

import ca.usask.agents.macrm.common.records._
import org.joda.time.DateTime
import java.io.Serializable

@SerialVersionUID(100L)
case class TaskDescription(val jobId: Int,
                           val index: Int,
                           val duration: DateTime,
                           val resource: Resource,
                           val relativeSubmissionTime: DateTime,
                           val constraints: List[Int]) extends Serializable

@SerialVersionUID(100L)
case class JobDescription(val jobId: Int,
                          val userId: UserId,
                          val numberOfTasks: Int,
                          var tasks: List[TaskDescription],
                          var constraints: List[Int]) extends Serializable