package ca.usask.agents.macrm.clustermanager.utils

import ca.usask.agents.macrm.common.records._

trait AbstractQueue {
    def isEmpty: Boolean

    def EnqueueRequest(e: Any): Unit

    def getBestMatches(resource: Resource, capability: List[Constraint]): Option[(List[JobDescription], List[TaskDescription])]
    
    def DequeueRequest(): Either[JobDescription,TaskDescription]
}

object AbstractQueue {
    def apply(queuetype: String): AbstractQueue = queuetype match {
        case "FIFOQueue" => new FIFOQueue()
    }
}
