package ca.usask.agents.macrm.common.records

import akka.actor._
import java.io.Serializable

/*
 * TODO: defualt port should be read from config
 */
@SerialVersionUID(100L)
class NodeId(val host: String,
             val port: Int,
             val agent: ActorRef)
    extends AgentsComaparable[NodeId] with Serializable {

    override def equal(input: Any): Boolean = input match {
        case that: NodeId => (this.host == that.host && this.port == that.port) || this.agent == that.agent
        case _            => false
    }

    override def toString() = "<host:" + host + " port:" + port.toString() + ">"
}

object NodeId {
    def apply(agent: ActorRef) = new NodeId("0.0.0.0", 0, agent)
    def apply(host: String, port: Int) = new NodeId(host, port, null)
}
