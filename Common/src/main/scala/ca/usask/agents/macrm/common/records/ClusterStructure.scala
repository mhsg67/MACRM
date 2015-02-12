package ca.usask.agents.macrm.common.records

import java.io.Serializable

@SerialVersionUID(100L)
class ClusterStructure extends Serializable{

    var clusterNodes: List[List[Int]] = null

    def getRandomSetOfNodes(constraints: Seq[NodeConstraint], samplingSize: Int): List[NodeId] = null

    def canHandleThisConstraint(constraint: NodeConstraint): Boolean = false
}