package ca.usask.agents.macrm.jobmanager.utils

import scala.concurrent.duration._
import ca.usask.agents.macrm.common.records._

/**
 * Holds the system configuration parameters for job manager project
 */
object JobManagerConfig {

    var userId: Int = 1
    var jobId: String = null

    def readConfigurationFile() = {
        Logger.Log("Start reading configuration file")

        /*
         * These are just for test, in the case of simulation
         * the central creater of JobManager specify these two, in
         * the case of real system, it should read configuration file
         */

        jobId = GUIDGenerator.getNextGUID

        Logger.Log("Finished reading configuration file")
    }

    def getClusterManagerAddress() = "akka.tcp://ClusterManagerAgent@" +
        clusterManagerIPAddress + ":" +
        clusterManagerAgentDefualtPort + "/" +
        "user/ClusterManagerAgent"

    def getResourceTrackerAddress() = "akka.tcp://ResourceTrackerAgent@" +
        trackerIPAddress + ":" +
        trackerDefualtPort + "/" +
        "user/ResourceTrackerAgent"

    /**
     * To access ClusterManager actor
     */
    val clusterManagerIPAddress = "127.0.1.1"
    val clusterManagerAgentDefualtPort = "2000"

    /**
     * To access resourceTracker actor
     */
    val trackerIPAddress = "127.0.1.1"
    val trackerDefualtPort = "3000"

    /**
     * After receiving a wave of task for scheduling
     * we start sampling, after 10 millis of that, if we still have
     * unschedule tasks we try to do sampling again
     */
    val samplingTimeout = 10 millis

    /**
     * If the samplingTimout for 2 times and the JobManager could not find
     * proper resources for some tasks of a wave , then it forward them to CM
     */
    val numberOfAllowedSamplingRetry = 2

    /**
     * There is no specific time, there is just a timeout
     * which is 600000 (10 min), on the other hand, when JobManager
     * send resource request or resource release it count as heartBeat
     *
     * We will send it every 1 min (60000 millis) with the first one
     * send 1 second into JobManager execution time
     */
    val heartBeatStartDelay = (samplingTimeout * numberOfAllowedSamplingRetry)
    val heartBeatInterval = 60000 millis
}