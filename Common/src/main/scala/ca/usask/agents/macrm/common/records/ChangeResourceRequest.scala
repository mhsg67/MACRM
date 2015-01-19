package ca.usask.agents.macrm.common.records

class ChangeResourceRequest(var resourceRequestId: ResourceRequestId,
                            var newPriority: Priority = null,
                            var newCapability: Resource = null,
                            var newNumContainers: Int = 0,
                            var newRelaxLocality: Boolean) {

    override def toString(): String = {
        "{ change to ID: " + resourceRequestId +
            ", new Priority: " + { if (newPriority != null) newPriority else "NO" } +
            ", new Capability: " + { if (newCapability != null) newCapability else "NO" } +
            ", new # Containers: " + newNumContainers +
            ", new Relax Locality " + { if (newRelaxLocality) "YES" else "NO" } + "}"
    }
}