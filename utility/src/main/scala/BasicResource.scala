package MACRM.utility


/*
 *TODO: these default number should be checked and put
 * into the configuration file
 */
class BasicResource(var numberOfVirtualCore: Int = 1, var memorySize: Int = 250) {
    def +(that: BasicResource): BasicResource = {
        new BasicResource(this.numberOfVirtualCore + that.numberOfVirtualCore, this.memorySize + that.memorySize)
    }

    def -(that: BasicResource): BasicResource = {
        new BasicResource(this.numberOfVirtualCore - that.numberOfVirtualCore, this.memorySize - that.memorySize)
    }
}
