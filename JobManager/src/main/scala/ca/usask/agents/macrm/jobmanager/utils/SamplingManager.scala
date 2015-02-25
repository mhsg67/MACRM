package ca.usask.agents.macrm.jobmanager.utils

import ca.usask.agents.macrm.common.records._
import akka.actor._

class SamplingManager{
    
    var samplingRate = 2
    var clusterNodes:List[(NodeId,List[Constraint])] = null 

    def loadSamplingInformation(samplingInformation:SamplingInformation){
        samplingRate = samplingInformation.samplingRate
        clusterNodes = samplingInformation.clusterNodes
    }
    
    def loadNewSamplingRate(newRate:Int) = samplingRate = newRate
    
    /**
     * @param: retry indicates whether the sampling is redoing for some tasks or not
     * @return: list of actorRef and the min amount of resource that should ask from them
     * these are dirctly use in _ResourceSamplingInquiry message
     */
    def getSamplingNode(tasksDescriptions:List[TaskDescription], retry:Boolean): List[(NodeId,Resource)] = null
    
    def canFindProperResourcesForTheseConstraints(constraints:List[Constraint]): Boolean = true
}