package ca.usask.agents.macrm.common.agents

import org.joda.time.DateTime
import akka.actor._
import akka.event.Logging

abstract class Agent extends Actor with ActorLogging {
    def Handle_UnknownMessage(mySelf: String, message: Any) = {
        log.error(mySelf + ": Unknown message => " + message.toString())
    }
}
