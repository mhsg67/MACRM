package ca.usask.agents.macrm.jobmanager.agents

import akka.actor._
import ca.usask.agents.macrm.jobmanager.utils._
import com.typesafe.config.ConfigFactory

/**
 * It is the starter for JobManager agent
 */
object main extends App {
    try {
        JobManagerConfig.readConfigurationFile()

        val system = ActorSystem.create("JobManagerAgent", ConfigFactory.load().getConfig("JobManagerAgent"))
        val jobManager = system.actorOf(Props[JobManagerAgent], name = "JobManagerAgent")

        jobManager ! "initiateEvent"

    }
    catch {
        case e: Exception => Logger.Error(e.toString())
    }
}
