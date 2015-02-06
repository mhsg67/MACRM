package ca.usask.agents.macrm.nodemanager.agents

import ca.usask.agents.macrm.nodemanager.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import scala.collection.mutable._
import org.joda.time.DateTime
import akka.actor._

class ContainerManagerAgent(val serverState: ActorRef) extends Agent {

    var resourceSmaplingInquiryList = MutableList[_ResourceSamplingInquiry]()
    var havePendingServing = false

    import context.dispatcher
    val checkContainersEvent = context.system.scheduler.schedule(NodeManagerConfig.allCheckStartDelay, NodeManagerConfig.checkContainersInterval, self, "checkContainersEvent")

    def receive = {
        case "initiateEvent"                   => Event_initiate
        case "checkContainersEvent"            => Event_checkContainers
        case "checkAvailableResourcesEvent"    => Event_checkAvailableResources
        case message: _ResourceSamplingInquiry => Handle_ResourceSamplingInquiry(message)
        case message: _ResourceSamplingCancel  => Handle_ResourceSamplingCancel(message)
        case message: _Resource                => Handle_Resource(message)
        case _                                 => Handle_UnknownMessage
    }

    def Event_initiate = {
        Logger.Log("ContainerManagerAgent Initialization")
    }

    def Event_checkContainers = serverState ! "checkContainersEvent"

    def Event_checkAvailableResources = serverState ! "checkAvailableResourcesEvent"

    def Handle_ResourceSamplingInquiry(message: _ResourceSamplingInquiry) = {
        resourceSmaplingInquiryList += message
        if (resourceSmaplingInquiryList.length == 1 && !havePendingServing)
            context.system.scheduler.scheduleOnce(NodeManagerConfig.firstCheckAvailableResources, self, "checkAvailableResourcesEvent")
    }

    def Handle_ResourceSamplingCancel(message: _ResourceSamplingCancel) = resourceSmaplingInquiryList = resourceSmaplingInquiryList.filter((x: _ResourceSamplingInquiry) => x._source == message._source)

    def Handle_Resource(message: _Resource) = {
        if (!tryServerResourceSamplingInquiry(message._resource))
            context.system.scheduler.scheduleOnce(NodeManagerConfig.checkAvailableResource, self, "checkAvailableResourcesEvent")
    }

    def tryServerResourceSamplingInquiry(resource: Resource): Boolean = {
        if (resource.isNotUsable() || (resourceSmaplingInquiryList(0)._requiredResource > resource))
            false
        else
            true
    }
}