package MACRM.utility

/*
 * TODO: 
 *  1. capability can be more complex class which also includes processing capability such as GPU or public IP address
 */
class Node(val nodeId: String, val rack: Rack, val ipAddress: String, val capability: BasicResource, var state: NodeState) {

    def AvailableResources(): BasicResource = capability - state.OcuppiedResources()

    override def toString = nodeId + "_" + state.toString() + "_" + ipAddress + "_" + rack.toString()
}
