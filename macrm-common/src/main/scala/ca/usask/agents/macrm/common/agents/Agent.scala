package MACRM.utility

import akka.actor._

abstract class Agent extends Actor {
    def Handle_UnknownMessage =
        {
            throw new Exception(self.toString() + ": unknown message")
        }
}
