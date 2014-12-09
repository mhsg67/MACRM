package ca.usask.agents.macrm.common.records

object GUIDGenerator {
    def GetNextGUID = java.util.UUID.randomUUID().toString()
}