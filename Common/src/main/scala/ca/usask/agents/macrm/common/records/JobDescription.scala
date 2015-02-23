package ca.usask.agents.macrm.common.records

import ca.usask.agents.macrm.common.records._
import org.joda.time.DateTime
import java.io.Serializable

@SerialVersionUID(100L)
case class JobDescription(val jobId: Long,
                          val userId: Int,
                          val numberOfTasks: Int,
                          var tasks: List[TaskDescription],
                          var constraints: List[Int]) extends Serializable