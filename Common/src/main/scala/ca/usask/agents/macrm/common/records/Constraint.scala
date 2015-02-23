package ca.usask.agents.macrm.common.records

import ca.usask.agents.macrm.common.records._
import java.io.Serializable

@SerialVersionUID(100L)
case class Constraint(name: String, operator: Int, value: Int) extends Serializable