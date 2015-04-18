package ca.usask.agents.macrm.jobmanager.agents

import ca.usask.agents.macrm.jobmanager.utils._
import ca.usask.agents.macrm.common.records._
import com.typesafe.config.ConfigFactory
import akka.actor._

/**
 * It is the starter for JobManager agent
 */
object main {

    def readConfiguration(): List[String] = {
        val file = scala.io.Source.fromFile("NodeManagerConfig.txt")
        val lines = file.getLines()
        lines.map(x => x.split(":")(1)).toList
    }

    def main(args: Array[String]) {
        val generalConfig = readConfiguration()

        JobManagerConfig.numberOfAllowedSamplingRetry = generalConfig(3).toInt
        JobManagerConfig.samplingTimeoutLong = generalConfig(2).toLong
        JobManagerConfig.resourceTrackerIPAddress = generalConfig(1)
        JobManagerConfig.clusterManagerIPAddress = generalConfig(0)
        val system = ActorSystem.create("JobManagerAgent", ConfigFactory.load().getConfig("JobManagerAgent"))
        //val jobManager = system.actorOf(Props(new JobManagerAgent(JobManagerConfig.userId, JobManagerConfig.jobId)), name = "JobManagerAgent")

        //jobManager ! "initiateEvent"

    }
}
