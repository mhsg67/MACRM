package ca.usask.agents.macrm.nodemanager.agents

import ca.usask.agents.macrm.nodemanager.utils._
import ca.usask.agents.macrm.common.records._
import ca.usask.agents.macrm.common.agents._
import scala.collection.mutable._
import org.joda.time.DateTime
import akka.actor._

class ContainerManagerAgent(val nodeManager: ActorRef, val serverState: ActorRef) extends Agent {

    var ignoreNextResourceMessage = false
    var ignoreResourceSamplingResponseTimeoutEvent = 0
    var resourceSmaplingInquiryList = Queue[_ResourceSamplingInquiry]()
    var recentlyServerdSamplingInquiry: _ResourceSamplingInquiry = null
    var havePendingServing = false

    import context.dispatcher
    val checkContainersEvent = context.system.scheduler.schedule(NodeManagerConfig.allCheckStartDelay, NodeManagerConfig.checkContainersInterval, self, "checkContainersEvent")

    def receive = {
        case "initiateEvent"                        => Event_initiate
        case "checkContainersEvent"                 => Event_checkContainers
        case "resourceSamplingResponseTimeoutEvent" => Event_resourceSamplingResponseTimeout
        case message: _ResourceSamplingInquiry      => Handle_ResourceSamplingInquiry(message)
        case message: _ResourceSamplingCancel       => Handle_ResourceSamplingCancel(message)
        case message: _Resource                     => Handle_Resource(message)
        case message: _AllocateContainerFromCM      => Handle_AllocateContainerFromCM(message)
        case message: _AllocateContainerFromJM      => Handle_AllocateContainerFromJM(message)
        case _                                      => Handle_UnknownMessage
    }

    def Event_initiate = {
        Logger.Log("ContainerManagerAgent Initialization")
    }

    def Event_checkContainers = serverState ! "checkContainersEvent"

    def Handle_ResourceSamplingInquiry(message: _ResourceSamplingInquiry) = {
        resourceSmaplingInquiryList += message
        if (resourceSmaplingInquiryList.length == 1)
            serverState ! "checkAvailableResourcesEvent"
    }

    def Handle_Resource(message: _Resource): Unit = {
        if (ignoreNextResourceMessage) {
            ignoreNextResourceMessage = false
            serverState ! "checkAvailableResourcesEvent"
        }
        else if (message._resource.isNotUsable())
            resourceSmaplingInquiryList = Queue()
        else if (resourceSmaplingInquiryList(0)._minRequiredResource > message._resource) {
            havePendingServing = true
            nodeManager ! new _ResourceSamplingResponse(self, DateTime.now(), message._resource)
            context.system.scheduler.scheduleOnce(NodeManagerConfig.waitForJMActionToResourceSamplingResponseTimeout, self, "resourceSamplingResponseTimeoutEvent")
        }
        else {
            resourceSmaplingInquiryList.dequeue()
            if (resourceSmaplingInquiryList.length > 0)
                Handle_Resource(message)
        }
    }

    def Event_resourceSamplingResponseTimeout = {
        if (ignoreResourceSamplingResponseTimeoutEvent>0)
            ignoreResourceSamplingResponseTimeoutEvent -=1
        else {
            havePendingServing = false
            resourceSmaplingInquiryList.dequeue()
            if (resourceSmaplingInquiryList.length == 1)
                serverState ! "checkAvailableResourcesEvent"
        }
    }

    def Handle_ResourceSamplingCancel(message: _ResourceSamplingCancel) = {
        if (resourceSmaplingInquiryList.length > 0) {
            if (resourceSmaplingInquiryList(0)._source == message._source) {
                resourceSmaplingInquiryList = resourceSmaplingInquiryList.filter((x) => x._source == message._source)
                if (havePendingServing) {
                    ignoreResourceSamplingResponseTimeoutEvent += 1
                    havePendingServing = false
                }
                else if (resourceSmaplingInquiryList.length > 0)
                    serverState ! "checkAvailableResourcesEvent"
            }
            else {
                resourceSmaplingInquiryList = resourceSmaplingInquiryList.filter((x) => x._source == message._source)
            }
        }
    }

    def Handle_AllocateContainerFromCM(message: _AllocateContainerFromCM) = {
        if(resourceSmaplingInquiryList.length > 0)
            ignoreNextResourceMessage = true
    }

    def Handle_AllocateContainerFromJM(message: _AllocateContainerFromJM) = {
        ignoreResourceSamplingResponseTimeoutEvent += 1
    }

}