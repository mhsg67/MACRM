package ca.usask.agents.macrm.common.records

import java.io.Serializable

@SerialVersionUID(100L)
class JobReport(val userId: UserId, val jobId: String) extends Serializable 
