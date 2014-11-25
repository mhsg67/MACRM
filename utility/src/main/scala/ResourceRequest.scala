package MACRM.utility

class BasicPriority(var priority: Int)

class BasicLocation(var nodeId: String = "*", var rackId: String = "*") {
    def isRelaxed: Boolean = nodeId.equals("*") && rackId.equals("*")
}

class ResourceRequest(var container: Container, var location: BasicLocation, var numberOfContainer: Int, var priority: BasicPriority = new BasicPriority(0))
