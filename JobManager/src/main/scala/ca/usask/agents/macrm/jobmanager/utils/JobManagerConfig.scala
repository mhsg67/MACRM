package ca.usask.agents.macrm.jobmanager.utils

import scala.concurrent.duration._
import ca.usask.agents.macrm.common.records._

/**
 * Holds the system configuration parameters for job manager project
 */
object JobManagerConfig {

    def getClusterManagerAddress() = "akka.tcp://ClusterManagerAgent@" + clusterManagerIPAddress + ":" + clusterManagerDefualtPort + "/user/ClusterManagerAgent"

    def getResourceTrackerAddress = "akka.tcp://ResourceTrackerAgent@" + resourceTrackerIPAddress + ":" + resourceTrackerDefualtPort + "/user/ResourceTrackerAgent"

    def createNodeManagerAddressString(host: String, port: Int) = "akka.tcp://NodeManagerAgent@" +
        host + ":" +
        port.toString() + "/" +
        "user/NodeManagerAgent"

    /**
     * To access ClusterManager actor
     */
    var clusterManagerIPAddress = "127.0.0.1"
    val clusterManagerDefualtPort = "2000"

    /**
     * To access resourceTracker actor
     */
    var resourceTrackerIPAddress = "127.0.0.1"
    val resourceTrackerDefualtPort = "3000"

    /**
     * After receiving a wave of task for scheduling
     * we start sampling, after 10 millis of that, if we still have
     * unschedule tasks we try to do sampling again
     */
    val samplingTimeout = 150 millis

    /**
     * If the samplingTimout for 2 times and the JobManager could not find
     * proper resources for some tasks of a wave , then it forward them to CM
     */
    val numberOfAllowedSamplingRetry = 1000
}