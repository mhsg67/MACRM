package ca.usask.agents.macrm.common.agents

import org.joda.time.DateTime
import akka.actor._
import ca.usask.agents.macrm.common.records.NodeReport

/**
 * From ResourceTracker to ClusterManager(QueueAgent) to inform it about
 * a node with free resources. QueueAgent use this for scheduling JobManager
 * of new submitted job
 */
case class _ServerWithEmptyResources(_source: ActorRef, _time: DateTime, _report: NodeReport)
    extends SystemMessage(_source, _time)

/**
 * From ResourceTracker to ClusterManager(QueueAgent) to inform it about
 * Sharing of cluster among users. It is useful when system is working in
 * centralize way
 *
 * TODO: Add information of user share into the message
 */
case class _EachUserShareOfCluster(_source: ActorRef, _time: DateTime)
    extends SystemMessage(_source, _time)

/**
 * From ClusterManager(SchedulerAgent) to ResourceTracker in order to
 * acquire cluster state
 */
case class _ClusterStateRequest(_source: ActorRef, _time: DateTime)
    extends SystemMessage(_source, _time)

/**
 * From ResourceTracker to ClusterManager(SchedulerAgent) in response to
 * _ClusterStateRequest message
 *
 * TODO: Add cluster state information in this message
 */
case class _ClusterStateRespond(_source: ActorRef, _time: DateTime)
    extends SystemMessage(_source, _time)