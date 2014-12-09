package ca.usask.agents.macrm.common.records

class ResourceRequest(var priority: Priority,
                      var capability: Resource,
                      var numContainers: Int,
                      var relaxLocality: Boolean,
                      var resourceName: String,
                      var resourceRequestId: ResourceRequestId) {
    override def toString(): String =
        "{ ID: " + resourceRequestId +
            ", Priority: " + priority +
            ", Capability: " + capability +
            ", # Containers: " + numContainers +
            ", Location: " + resourceName +
            ", Relax Locality: " + relaxLocality + "}"
}
