package ca.usask.agents.macrm.clustermanager.utils

import ca.usask.agents.macrm.common.records._

class ClusterStructure {

    var samplingRate = 2
    var clusterNodes = List[NodeReport]()

    def updateClusterStructure(newSamplingRate: Int = -1, removedServers: List[NodeId] = null, addedServers: List[NodeReport] = null) = {
        if (newSamplingRate != -1) samplingRate = newSamplingRate
        if (removedServers != null) clusterNodes = clusterNodes.filterNot(x => removedServers.exists(y => y == x.nodeId))
        if (addedServers != null) {
            clusterNodes = clusterNodes.filterNot(x => addedServers.exists(y => y.nodeId == x.nodeId))
            clusterNodes ++= addedServers
        }
    }

    //TODO:test it
    def getCurrentSamplingInformation(constraints: List[Constraint]): SamplingInformation =
        new SamplingInformation(samplingRate, clusterNodes.map(x => (x.nodeId, getMatchCapablitiesOfNode(x.otherCapablity, constraints))))

    def getMatchCapablitiesOfNode(nodeCapabilities: List[Constraint], jobConstraints: List[Constraint]) = {
        val result = nodeCapabilities.filter(x => doesCapabilityMatchConstraints(x, jobConstraints))
        result match {
            case List() => null
            case _      => result
        }
    }

    def doesCapabilityMatchConstraints(capability: Constraint, jobConstraints: List[Constraint]) = {
        val temp = jobConstraints.filter(x => x.name == capability.name)
        temp match {
            case List() => false
            case _      => temp.foldLeft(true)((x, y) => x && doseCapabilityMatchConstraint(capability, y))
        }
    }

    def doseCapabilityMatchConstraint(capability: Constraint, constraint: Constraint) = constraint.operator match {
        case 0 => if (capability.value == constraint.value) true else false
        case 1 => if (capability.value != constraint.value) true else false
        case 2 => if (capability.value < constraint.value) true else false
        case 3 => if (capability.value > constraint.value) true else false
    }

}