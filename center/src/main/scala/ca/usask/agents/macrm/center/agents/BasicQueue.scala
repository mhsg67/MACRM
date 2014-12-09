package ca.usask.agents.macrm.center.agents

import ca.usask.agents.macrm.common.agents._
import scala.collection.mutable._

trait BasicQueue[T]{
    private var requestQueue = Queue[T]()
    
    def Enqueue(_element:T) = requestQueue.enqueue(_element)
    def Dequeue():T = requestQueue.dequeue() 
}
