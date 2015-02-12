package ca.usask.agents.macrm.common.records

import java.io.Serializable

sealed trait NodeConstraint

@SerialVersionUID(100L)
case class GPU() extends NodeConstraint with Serializable

@SerialVersionUID(100L)
case class PUBLICIP() extends NodeConstraint with Serializable

object NodeConstraint {
    def apply(constraintType: String): NodeConstraint = constraintType match {
        case "GPU"      => new GPU()
        case "PUBLICIP" => new PUBLICIP()
    }
}

