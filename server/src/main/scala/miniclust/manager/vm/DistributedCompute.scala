package miniclust.manager.vm

import miniclust.manager.ml.*
import scala.collection.mutable
import scala.concurrent.{Future, ExecutionContext}
import io.circe.{Codec, Decoder, Encoder}
import io.circe.generic.semiauto.*

/**
 * Distributed compute layer for the Civic Angel system
 * 
 * Provides distributed computation capabilities across multiple nodes
 */

/**
 * Compute node in the distributed system
 */
case class ComputeNode(
  id: String,
  hostname: String,
  port: Int,
  status: NodeStatus = NodeStatus.Active,
  capabilities: NodeCapabilities,
  load: NodeLoad = NodeLoad(0.0, 0.0, 0)
) derives Codec

/**
 * Node status
 */
enum NodeStatus:
  case Active
  case Busy
  case Offline
  case Maintenance

object NodeStatus:
  given Codec[NodeStatus] = deriveCodec[NodeStatus]

/**
 * Node capabilities
 */
case class NodeCapabilities(
  cpuCores: Int,
  memoryGB: Double,
  gpuAvailable: Boolean = false,
  maxConcurrentTasks: Int = 4
) derives Codec

/**
 * Node load metrics
 */
case class NodeLoad(
  cpuUsage: Double,  // 0.0 to 1.0
  memoryUsage: Double,  // 0.0 to 1.0
  activeTasks: Int
) derives Codec

/**
 * Distributed task
 */
case class DistributedTask(
  id: String,
  name: String,
  taskType: TaskType,
  payload: TaskPayload,
  assignedNode: Option[String] = None,
  status: TaskStatus = TaskStatus.Pending
)

/**
 * Task types
 */
enum TaskType:
  case ModelInference    // Run ML inference
  case TensorComputation // Tensor operations
  case DataProcessing    // Data transformation
  case AgentActivation   // Activate Civic Angel agents

object TaskType:
  given Codec[TaskType] = deriveCodec[TaskType]

/**
 * Task payload - data for the task
 */
sealed trait TaskPayload

case class InferencePayload(
  modelId: String,
  input: Tensor
) extends TaskPayload

case class TensorComputePayload(
  operation: String,
  tensors: List[Tensor]
) extends TaskPayload

case class DataPayload(
  data: Array[Float]
) extends TaskPayload

/**
 * Task status
 */
enum TaskStatus:
  case Pending
  case Assigned
  case Running
  case Completed
  case Failed

object TaskStatus:
  given Codec[TaskStatus] = deriveCodec[TaskStatus]

/**
 * Task result
 */
case class TaskResult(
  taskId: String,
  success: Boolean,
  resultData: Option[List[Float]],  // Changed from Tensor to List[Float]
  error: Option[String] = None,
  executionTimeMs: Long
) derives Codec

/**
 * Distributed compute coordinator
 */
class DistributedComputeCoordinator:
  private val nodes = mutable.Map[String, ComputeNode]()
  private val tasks = mutable.Map[String, DistributedTask]()
  private val results = mutable.Map[String, TaskResult]()
  private var taskCounter = 0
  
  /**
   * Register a compute node
   */
  def registerNode(node: ComputeNode): Unit =
    nodes(node.id) = node
  
  /**
   * Unregister a compute node
   */
  def unregisterNode(nodeId: String): Option[ComputeNode] =
    nodes.remove(nodeId)
  
  /**
   * Submit a distributed task
   */
  def submitTask(
    name: String,
    taskType: TaskType,
    payload: TaskPayload
  ): String =
    taskCounter += 1
    val taskId = s"task-$taskCounter"
    
    val task = DistributedTask(
      id = taskId,
      name = name,
      taskType = taskType,
      payload = payload
    )
    
    tasks(taskId) = task
    
    // Try to assign to a node
    scheduleTask(taskId)
    
    taskId
  
  /**
   * Schedule task to an available node
   */
  private def scheduleTask(taskId: String): Unit =
    tasks.get(taskId).foreach { task =>
      // Find node with lowest load
      val availableNode = nodes.values
        .filter(_.status == NodeStatus.Active)
        .filter(n => n.load.activeTasks < n.capabilities.maxConcurrentTasks)
        .toList
        .sortBy(_.load.cpuUsage)
        .headOption
      
      availableNode.foreach { node =>
        val assignedTask = task.copy(
          assignedNode = Some(node.id),
          status = TaskStatus.Assigned
        )
        tasks(taskId) = assignedTask
        
        // Update node load
        val updatedLoad = node.load.copy(activeTasks = node.load.activeTasks + 1)
        nodes(node.id) = node.copy(load = updatedLoad)
      }
    }
  
  /**
   * Execute a task (simulated for now)
   */
  def executeTask(taskId: String): Option[TaskResult] =
    tasks.get(taskId).flatMap { task =>
      task.assignedNode.flatMap { nodeId =>
        nodes.get(nodeId).map { node =>
          val startTime = System.currentTimeMillis()
          
          // Update task status
          tasks(taskId) = task.copy(status = TaskStatus.Running)
          
          // Simulate execution based on task type
          val result = task.payload match
            case InferencePayload(modelId, input) =>
              // Simulate model inference
              Some(input * 0.5f) // Dummy result
            
            case TensorComputePayload(operation, tensors) =>
              // Simulate tensor operation
              tensors.headOption
            
            case DataPayload(data) =>
              // Simulate data processing
              Some(Tensor(TensorShape.vector(data.length), data))
            
            case _ =>
              None
          
          val endTime = System.currentTimeMillis()
          val executionTime = endTime - startTime
          
          // Mark task completed
          tasks(taskId) = task.copy(status = TaskStatus.Completed)
          
          // Update node load
          val updatedLoad = node.load.copy(activeTasks = node.load.activeTasks - 1)
          nodes(nodeId) = node.copy(load = updatedLoad)
          
          val taskResult = TaskResult(
            taskId = taskId,
            success = true,
            resultData = result.map(_.data.toList),  // Convert tensor to list
            executionTimeMs = executionTime
          )
          
          results(taskId) = taskResult
          taskResult
        }
      }
    }
  
  /**
   * Get task result
   */
  def getTaskResult(taskId: String): Option[TaskResult] =
    results.get(taskId)
  
  /**
   * Get task status
   */
  def getTaskStatus(taskId: String): Option[TaskStatus] =
    tasks.get(taskId).map(_.status)
  
  /**
   * List all nodes
   */
  def listNodes: List[ComputeNode] =
    nodes.values.toList
  
  /**
   * List all tasks
   */
  def listTasks: List[DistributedTask] =
    tasks.values.toList
  
  /**
   * Get coordinator statistics
   */
  def getStats: DistributedComputeStats =
    DistributedComputeStats(
      totalNodes = nodes.size,
      activeNodes = nodes.values.count(_.status == NodeStatus.Active),
      totalTasks = tasks.size,
      pendingTasks = tasks.values.count(_.status == TaskStatus.Pending),
      runningTasks = tasks.values.count(_.status == TaskStatus.Running),
      completedTasks = tasks.values.count(_.status == TaskStatus.Completed),
      averageCpuUsage = if nodes.nonEmpty then nodes.values.map(_.load.cpuUsage).sum / nodes.size else 0.0,
      averageMemoryUsage = if nodes.nonEmpty then nodes.values.map(_.load.memoryUsage).sum / nodes.size else 0.0
    )

/**
 * Distributed compute statistics
 */
case class DistributedComputeStats(
  totalNodes: Int,
  activeNodes: Int,
  totalTasks: Int,
  pendingTasks: Int,
  runningTasks: Int,
  completedTasks: Int,
  averageCpuUsage: Double,
  averageMemoryUsage: Double
) derives Codec
