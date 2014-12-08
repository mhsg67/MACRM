package ca.usask.agents.macrm.common.records

import akka.actor._

/*
 * TODO: defualt port should be read from config
 */
class NodeId(var host: String = "127.0.0.1", var port: Int = 2000, var agent:ActorRef) extends AgentsComaparable[NodeId] {
    override def ==(other: NodeId): Boolean = {
        if (this.host == other.host || this.agent == other.agent)
            return true;
        else
            return false;
    }
}