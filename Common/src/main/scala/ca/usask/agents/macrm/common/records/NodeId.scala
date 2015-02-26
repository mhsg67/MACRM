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
    extends Serializable {

    override def equals(input: Any): Boolean = input match {
        case that: NodeId => this.agent match {
            case null => this.host == that.host && this.port == that.port
            case _    => this.agent == that.agent
        }
        case _ => false
    }

    override def toString() = "<host:" + host + " port:" + port.toString() + ">"
}

object NodeId {
    def apply(host: String, port: Int, agent: ActorRef): NodeId = new NodeId(host, port, agent)
    def apply(agent: ActorRef): NodeId = apply("0.0.0.0", 0, agent)
    def apply(host: String, port: Int): NodeId = apply(host, port, null)
}