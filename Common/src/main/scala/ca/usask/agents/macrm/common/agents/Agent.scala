package ca.usask.agents.macrm.common.agents

import org.joda.time.DateTime
import akka.actor._
import akka.event.Logging

abstract class Agent extends Actor with ActorLogging {

    override def preStart() = {
        log.debug(self.toString() + ": Starting")
    }
    
    def Handle_UnknownMessage = {
        log.debug(self.toString() + ": Unknown message")
        log.error(self.toString() + ": Unknown message")
    }   
}
