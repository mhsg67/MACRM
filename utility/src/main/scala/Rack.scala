package MACRM.utility

import scala.collection.mutable._

class Rack(val rackId: String, var listOfNodes: ListBuffer[Node]) {
    override def toString = rackId
}
