package ca.usask.agents.macrm.common.records

/*trait AgentsComaparable[T] extends Ordered[T]{
    override def compare(other:T) = 0;
    def ==(other:T):Boolean = false;
}*/

trait AgentsComaparable[T] {
    //override def compare(other:T) = 0;
    def equal(other:T):Boolean;
}