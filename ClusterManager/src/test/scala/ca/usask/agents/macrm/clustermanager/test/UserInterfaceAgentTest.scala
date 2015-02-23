package ca.usask.agents.macrm.clustermanager.test

import ca.usask.agents.macrm.clustermanager.agents._
import ca.usask.agents.macrm.common.records._

class UserInterfaceAgentTest extends UnitSpec{
    
    "getJobDescription" should "return JobDescription Object" in {
        val agent = new UserInterfaceAgent(null)    
        
        val result:Either[String, JobDescription] = agent.getJobDescription("{\"JI\": 1,\"UI\": 1,\"TS\":[{\"INX\":0,\"DUR\":100,\"RST\":2,\"CPU\":1,\"MEM\":250,\"PRI\":0,\"TSC\":1},{\"INX\":1,\"DUR\":100,\"RST\":2,\"CPU\":1,\"MEM\":250,\"PRI\":0,\"TSC\":1}],\"CS\": null}")
        
        
        
        result should be ('right)
        result.right.get.jobId should be (1)
    }
    
    "getJobDescription" should "return JobDescription Object" in {
        val agent = new UserInterfaceAgent(null)    
        
        val result:Either[String, JobDescription] = agent.getJobDescription("{\"JI\": 1,\"UI\": 1,\"TS\":[{\"INX\":0,\"DUR\":100,\"RST\":2,\"CPU\":1,\"MEM\":250,\"PRI\":0,\"TSC\":1},{\"INX\":1,\"DUR\":100,\"RST\":2,\"CPU\":1,\"MEM\":250,\"PRI\":0,\"TSC\":1},{\"INX\":2,\"DUR\":200,\"RST\":3,\"CPU\":1,\"MEM\":500,\"PRI\":0,\"TSC\":1}],\"CS\":[{\"INX\":0,\"V1\":1,\"OP\":0,\"V2\":1},{\"INX\":0,\"V1\":2,\"OP\":0,\"V2\":2},{\"INX\":0,\"V1\":3,\"OP\":0,\"V2\":3},{\"INX\":1,\"V1\":1,\"OP\":0,\"V2\":2},{\"INX\":1,\"V1\":2,\"OP\":0,\"V2\":1},{\"INX\":2,\"V1\":1,\"OP\":0,\"V2\":3},]}")
        
        result should be ('right)
        result.right.get.constraints should be (List(1,2,3))
        
    }
}