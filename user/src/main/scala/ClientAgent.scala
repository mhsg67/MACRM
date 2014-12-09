package MACRM.user

import ca.usask.agents.macrm.common.agents._
import akka.actor._

class ClientAgent extends Agent {
    def receive =
        {
            case _ => Handle_UnknownMessage
        }

}
