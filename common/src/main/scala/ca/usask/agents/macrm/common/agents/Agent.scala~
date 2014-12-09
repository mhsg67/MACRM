package ca.usask.agents.macrm.common.agents

import akka.actor._

abstract class Agent extends Actor {
    def Handle_UnknownMessage =
        {
            throw new Exception(self.toString() + ": unknown message")
        }
}
