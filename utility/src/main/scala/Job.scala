package MACRM.utility

/*
 * TODO: complete this class
 *  1. add list of its jobs
 *  2. 
 */
class BasicUser(val id: Int = -1)

/*
 * TODO: 
 *  1. handle dependency of task with other tasks
 *  2. 
 * ~TODO:
 *  -1. There should not be anything about resources since it causes 
 *  parallel definition with resource class 
 */
class BasicTask(val id: Int = -1)

/*
 * TODO: Complete this class
 *  0. add list of tasks
 *  1. add list of container (MAYBE)
 *  2. add ref to the Application Master of this job
 *  3. add priority (MAYBE)
 */
class Job(val user: BasicUser, val id: Int = -1) 
