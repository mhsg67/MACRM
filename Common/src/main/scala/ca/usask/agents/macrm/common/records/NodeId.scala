package ca.usask.agents.macrm.common.records

import akka.actor._

/*
 * TODO: defualt port should be read from config
 */
class NodeId(val host: String = "127.0.0.1", val port: Int = 4000, val agent:ActorRef) extends AgentsComaparable[NodeId] {
    override def equal(other: NodeId): Boolean = {
        if (this.host == other.host || this.agent == other.agent)
            return true;
        else
            return false;
    }
}
