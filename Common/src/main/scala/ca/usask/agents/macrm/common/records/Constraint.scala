package ca.usask.agents.macrm.common.records

import ca.usask.agents.macrm.common.records._
import java.io.Serializable

@SerialVersionUID(100L)
case class Constraint(val name: String,
                      val operator: Int,
                      val value: Int) extends Serializable {

    override def toString() = "<" + name + {
        operator match {
            case 0 => "="
            case 1 => "!="
            case 2 => "<"
            case 3 => ">"
        }
    } + value.toString() + ">"
}