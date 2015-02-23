package ca.usask.agents.macrm.clustermanager.test

import ca.usask.agents.macrm.clustermanager.agents._
import ca.usask.agents.macrm.common.records._
import org.scalatest._

class QueueTest extends UnitSpec {

    "getFirstOrBestMatchJob" should "get the first element of the queue" in {
        val queue = AbstractQueue("FIFOQueue")
        
        val task1 = new TaskDescription(null, 1, 0, new org.joda.time.Duration(100), new Resource(1, 250), new org.joda.time.Duration(2), null)
        val task2 = new TaskDescription(null, 1, 1, new org.joda.time.Duration(200), new Resource(1, 250), new org.joda.time.Duration(2), null)
        val job1 = new JobDescription(1, 1, 2, List(task1, task2), null)       
        queue.EnqueueJob(job1)
        
        val resource = new Resource(3,1000)
        val capability = List()
        val result = queue.getFirstOrBestMatchJob(resource, capability) 
        result should not be None 
        result.get.numberOfTasks should be (2)
    }
    
    "doesJobDescriptionMatch" should "return right values" in {
        val queue = AbstractQueue("FIFOQueue")
        
        val task1 = new TaskDescription(null, 1, 0, new org.joda.time.Duration(100), new Resource(1, 250), new org.joda.time.Duration(2), List(1))       
        val job1 = new JobDescription(1, 1, 1, List(task1), List(1))       
        queue.EnqueueJob(job1)
        
        val resource = new Resource(3,1000)
        val capability0 = null
        var result = queue.doesJobDescriptionMatch(resource,capability0,job1)
        result should be (false)        
        
        val capability1 = List(2)
        result = queue.doesJobDescriptionMatch(resource,capability1,job1)
        result should be (false)
        
        val capability2 = List(1)
        result = queue.doesJobDescriptionMatch(resource,capability2,job1)
        result should be (true)
    }
    
    "RemoveJob" should "should remove inserted job" in {
        val queue = AbstractQueue("FIFOQueue")
        
        val task1 = new TaskDescription(null, 1, 0, new org.joda.time.Duration(100), new Resource(1, 250), new org.joda.time.Duration(2), null)
        val task2 = new TaskDescription(null, 1, 1, new org.joda.time.Duration(200), new Resource(1, 250), new org.joda.time.Duration(2), null)
        val job1 = new JobDescription(1, 1, 2, List(task1, task2), null)       
        queue.EnqueueJob(job1)
        
        val resource = new Resource(3,1000)
        val capability = List()
        var result = queue.getFirstOrBestMatchJob(resource, capability)
        result should not be (None)
        queue.RemoveJob(result.get)
        result = queue.getFirstOrBestMatchJob(resource, capability)
        result should be (None)
    }
}