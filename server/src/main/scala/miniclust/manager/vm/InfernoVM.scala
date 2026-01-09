package miniclust.manager.vm

import miniclust.manager.ml.*
import miniclust.manager.civicangel.Agent
import scala.collection.mutable
import io.circe.{Codec, Decoder, Encoder}
import io.circe.generic.semiauto.*

/**
 * Inferno-inspired VM deployment engine
 * 
 * Inspired by Inferno OS and Plan 9, this VM provides:
 * - Namespace management (like Plan 9's union directories)
 * - Process/agent deployment
 * - Resource isolation
 * - Distributed compute primitives
 */

/**
 * VM Resource types
 */
enum ResourceType:
  case Compute    // CPU/processing resources
  case Memory     // RAM allocation
  case Storage    // Persistent storage
  case Network    // Network bandwidth
  case Agent      // Civic Angel agent allocation

object ResourceType:
  given Codec[ResourceType] = deriveCodec[ResourceType]

/**
 * Resource allocation specification
 */
case class ResourceSpec(
  resourceType: ResourceType,
  amount: Double,
  unit: String
) derives Codec

/**
 * VM namespace - hierarchical resource organization
 */
case class VMNamespace(
  name: String,
  path: String,
  resources: mutable.Map[String, Any] = mutable.Map.empty,
  children: mutable.Map[String, VMNamespace] = mutable.Map.empty
):
  def mount(childPath: String, namespace: VMNamespace): Unit =
    children(childPath) = namespace
  
  def unmount(childPath: String): Option[VMNamespace] =
    children.remove(childPath)
  
  def lookup(path: String): Option[Any] =
    if path == "/" then Some(this)
    else
      val parts = path.split("/").filter(_.nonEmpty)
      parts.headOption.flatMap { head =>
        children.get(head).flatMap { child =>
          if parts.length == 1 then Some(child)
          else child.lookup(parts.tail.mkString("/"))
        }
      }

/**
 * VM Process - unit of computation
 */
case class VMProcess(
  id: String,
  name: String,
  namespace: VMNamespace,
  state: ProcessState = ProcessState.Created,
  resources: List[ResourceSpec] = List.empty
):
  def start(): VMProcess = copy(state = ProcessState.Running)
  def stop(): VMProcess = copy(state = ProcessState.Stopped)
  def terminate(): VMProcess = copy(state = ProcessState.Terminated)

/**
 * Process states
 */
enum ProcessState:
  case Created
  case Running
  case Stopped
  case Terminated
  case Failed

object ProcessState:
  given Codec[ProcessState] = deriveCodec[ProcessState]

/**
 * VM Deployment - a deployed model or agent
 */
case class VMDeployment(
  id: String,
  name: String,
  deploymentType: DeploymentType,
  process: VMProcess,
  metadata: Map[String, String] = Map.empty
)

/**
 * Deployment types
 */
enum DeploymentType:
  case NeuralNetwork   // ML model deployment
  case CivicAgent      // Civic Angel agent deployment
  case DistributedTask // Distributed computation task
  case Service         // Generic service

object DeploymentType:
  given Codec[DeploymentType] = deriveCodec[DeploymentType]

/**
 * The Inferno VM - main orchestrator
 */
class InfernoVM:
  private val rootNamespace = VMNamespace("root", "/")
  private val processes = mutable.Map[String, VMProcess]()
  private val deployments = mutable.Map[String, VMDeployment]()
  private var processCounter = 0
  
  /**
   * Initialize the VM with default namespaces
   */
  def initialize(): Unit =
    // Create standard namespaces (Plan 9 style)
    rootNamespace.mount("agents", VMNamespace("agents", "/agents"))
    rootNamespace.mount("models", VMNamespace("models", "/models"))
    rootNamespace.mount("compute", VMNamespace("compute", "/compute"))
    rootNamespace.mount("data", VMNamespace("data", "/data"))
  
  /**
   * Deploy a neural network model
   */
  def deployModel(
    name: String,
    model: NeuralNetworkModel,
    resources: List[ResourceSpec] = List.empty
  ): String =
    processCounter += 1
    val processId = s"proc-$processCounter"
    
    val modelNamespace = VMNamespace(name, s"/models/$name")
    modelNamespace.resources("model") = model
    
    rootNamespace.children("models").mount(name, modelNamespace)
    
    val process = VMProcess(
      id = processId,
      name = s"model-$name",
      namespace = modelNamespace,
      resources = resources
    ).start()
    
    processes(processId) = process
    
    val deployment = VMDeployment(
      id = s"deploy-$processCounter",
      name = name,
      deploymentType = DeploymentType.NeuralNetwork,
      process = process,
      metadata = Map(
        "architecture" -> model.metadata.architecture,
        "parameterCount" -> model.parameterCount.toString
      )
    )
    
    deployments(deployment.id) = deployment
    deployment.id
  
  /**
   * Deploy a Civic Angel agent
   */
  def deployAgent(
    name: String,
    agent: Agent,
    resources: List[ResourceSpec] = List.empty
  ): String =
    processCounter += 1
    val processId = s"proc-$processCounter"
    
    val agentNamespace = VMNamespace(name, s"/agents/$name")
    agentNamespace.resources("agent") = agent
    
    rootNamespace.children("agents").mount(name, agentNamespace)
    
    val process = VMProcess(
      id = processId,
      name = s"agent-$name",
      namespace = agentNamespace,
      resources = resources
    ).start()
    
    processes(processId) = process
    
    val deployment = VMDeployment(
      id = s"deploy-$processCounter",
      name = name,
      deploymentType = DeploymentType.CivicAgent,
      process = process,
      metadata = Map(
        "agentId" -> agent.id,
        "agentType" -> agent.getClass.getSimpleName
      )
    )
    
    deployments(deployment.id) = deployment
    deployment.id
  
  /**
   * Execute a computation on a deployed model
   */
  def execute(deploymentId: String, input: Tensor): Option[Tensor] =
    for
      deployment <- deployments.get(deploymentId)
      model <- deployment.process.namespace.resources.get("model").collect {
        case m: NeuralNetworkModel => m
      }
    yield model.predict(input)
  
  /**
   * Get deployment information
   */
  def getDeployment(deploymentId: String): Option[VMDeployment] =
    deployments.get(deploymentId)
  
  /**
   * List all deployments
   */
  def listDeployments: List[VMDeployment] =
    deployments.values.toList
  
  /**
   * List deployments by type
   */
  def listDeploymentsByType(deploymentType: DeploymentType): List[VMDeployment] =
    deployments.values.filter(_.deploymentType == deploymentType).toList
  
  /**
   * Stop a deployment
   */
  def stopDeployment(deploymentId: String): Boolean =
    deployments.get(deploymentId).exists { deployment =>
      val stoppedProcess = deployment.process.stop()
      processes(stoppedProcess.id) = stoppedProcess
      true
    }
  
  /**
   * Terminate a deployment
   */
  def terminateDeployment(deploymentId: String): Boolean =
    deployments.get(deploymentId).exists { deployment =>
      val terminatedProcess = deployment.process.terminate()
      processes.remove(terminatedProcess.id)
      deployments.remove(deploymentId)
      true
    }
  
  /**
   * Get VM statistics
   */
  def getStats: VMStats =
    VMStats(
      totalProcesses = processes.size,
      runningProcesses = processes.values.count(_.state == ProcessState.Running),
      totalDeployments = deployments.size,
      deploymentsByType = Map(
        "neuralNetwork" -> deployments.values.count(_.deploymentType == DeploymentType.NeuralNetwork),
        "civicAgent" -> deployments.values.count(_.deploymentType == DeploymentType.CivicAgent),
        "distributedTask" -> deployments.values.count(_.deploymentType == DeploymentType.DistributedTask),
        "service" -> deployments.values.count(_.deploymentType == DeploymentType.Service)
      )
    )

/**
 * VM Statistics
 */
case class VMStats(
  totalProcesses: Int,
  runningProcesses: Int,
  totalDeployments: Int,
  deploymentsByType: Map[String, Int]
) derives Codec

/**
 * Deployment result
 */
case class DeploymentResult(
  deploymentId: String,
  success: Boolean,
  message: String
) derives Codec
