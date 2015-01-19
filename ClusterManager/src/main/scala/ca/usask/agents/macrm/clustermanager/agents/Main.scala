package ca.usask.agents.macrm.clustermanager.agents

import akka.actor._
import ca.usask.agents.macrm.clustermanager.utils._

/**
 * It is the starter for Central Resource Manager
 */
object main extends App{
    try
    {
         CenterConfig.readConfigurationFile()
         
         val system = ActorSystem("ResourceManager")         
    }
    catch
    {
        case e:Exception => Logger.Error(e.toString())
    }

}