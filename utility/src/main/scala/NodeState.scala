package MACRM.utility

import scala.collection.mutable._

/*
 * TODO:
 *  1. implement methods
 *  2. add number of pending request from AM which indicates the level of competition on this node
 */
abstract class NodeState {

    private var listOfContainers: ListBuffer[Container] = new ListBuffer[Container]()

    def AddContainer(newContainer: Container): Unit

    def RemoveContainer(container: Container): Boolean

    def RemoveContainer(containerId: Int): Boolean

    def TotalUtilization(): BasicUtilization

    def OcuppiedResources(): BasicResource

    override def toString() = " ";
}
