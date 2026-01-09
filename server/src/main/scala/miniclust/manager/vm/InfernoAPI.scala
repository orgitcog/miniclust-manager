package miniclust.manager.vm

import miniclust.manager.ml.*
import io.circe.generic.auto.*
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*

/**
 * API definitions for Inferno VM and ML operations
 */
object InfernoAPI:
  
  /**
   * Base endpoint for Inferno VM operations
   */
  private val infernoBase = endpoint
    .in("inferno")
    .tag("Inferno VM")
  
  /**
   * Get VM statistics
   * GET /inferno/stats
   */
  val vmStatsEndpoint = infernoBase
    .get
    .in("stats")
    .out(jsonBody[VMStats])
    .description("Get Inferno VM statistics")
  
  /**
   * Deploy a neural network model
   * POST /inferno/deploy/model
   */
  val deployModelEndpoint = infernoBase
    .post
    .in("deploy" / "model")
    .in(jsonBody[ModelDeployRequest])
    .out(jsonBody[DeploymentResult])
    .description("Deploy a neural network model to the VM")
  
  /**
   * Execute inference on a deployed model
   * POST /inferno/execute/{deploymentId}
   */
  val executeModelEndpoint = infernoBase
    .post
    .in("execute" / path[String]("deploymentId"))
    .in(jsonBody[InferenceRequest])
    .out(jsonBody[InferenceResponse])
    .errorOut(stringBody)
    .description("Execute inference on a deployed model")
  
  /**
   * List all deployments
   * GET /inferno/deployments
   */
  val listDeploymentsEndpoint = infernoBase
    .get
    .in("deployments")
    .out(jsonBody[List[DeploymentInfo]])
    .description("List all deployments in the VM")
  
  /**
   * Terminate a deployment
   * DELETE /inferno/deployments/{deploymentId}
   */
  val terminateDeploymentEndpoint = infernoBase
    .delete
    .in("deployments" / path[String]("deploymentId"))
    .out(jsonBody[TerminationResult])
    .description("Terminate a deployment")
  
  /**
   * Get distributed compute statistics
   * GET /inferno/compute/stats
   */
  val computeStatsEndpoint = infernoBase
    .get
    .in("compute" / "stats")
    .out(jsonBody[DistributedComputeStats])
    .description("Get distributed compute statistics")
  
  /**
   * Register a compute node
   * POST /inferno/compute/nodes
   */
  val registerNodeEndpoint = infernoBase
    .post
    .in("compute" / "nodes")
    .in(jsonBody[ComputeNode])
    .out(jsonBody[NodeRegistrationResult])
    .description("Register a new compute node")
  
  /**
   * Submit a distributed task
   * POST /inferno/compute/tasks
   */
  val submitTaskEndpoint = infernoBase
    .post
    .in("compute" / "tasks")
    .in(jsonBody[TaskSubmissionRequest])
    .out(jsonBody[TaskSubmissionResult])
    .description("Submit a task for distributed execution")
  
  /**
   * Get task result
   * GET /inferno/compute/tasks/{taskId}
   */
  val getTaskResultEndpoint = infernoBase
    .get
    .in("compute" / "tasks" / path[String]("taskId"))
    .out(jsonBody[TaskResult])
    .errorOut(stringBody)
    .description("Get the result of a task")
  
  /**
   * Build a neural network model
   * POST /inferno/models/build
   */
  val buildModelEndpoint = infernoBase
    .post
    .in("models" / "build")
    .in(jsonBody[ModelBuildRequest])
    .out(jsonBody[ModelBuildResponse])
    .description("Build a neural network model")

/**
 * Request/Response types for the API
 */

case class ModelDeployRequest(
  name: String,
  modelType: String,  // "feedforward", "classifier", etc.
  inputSize: Int,
  hiddenSizes: List[Int],
  outputSize: Int
)

case class InferenceRequest(
  data: List[Float]
)

case class InferenceResponse(
  success: Boolean,
  result: Option[List[Float]],
  error: Option[String]
)

case class DeploymentInfo(
  id: String,
  name: String,
  deploymentType: String,
  status: String,
  metadata: Map[String, String]
)

case class TerminationResult(
  success: Boolean,
  message: String
)

case class NodeRegistrationResult(
  success: Boolean,
  nodeId: String,
  message: String
)

case class TaskSubmissionRequest(
  name: String,
  taskType: String,
  modelId: Option[String] = None,
  data: Option[List[Float]] = None
)

case class TaskSubmissionResult(
  taskId: String,
  status: String
)

case class ModelBuildRequest(
  modelType: String,
  inputSize: Int,
  hiddenSizes: List[Int],
  outputSize: Int
)

case class ModelBuildResponse(
  success: Boolean,
  metadata: Option[ModelMetadata],
  architecture: Option[String],
  parameterCount: Option[Int]
)
