package miniclust.manager.vm

import miniclust.manager.ml.*
import sttp.tapir.server.ServerEndpoint
import sttp.shared.Identity

/**
 * Endpoint implementations for Inferno VM and ML operations
 */
class InfernoEndpoints(vm: InfernoVM, compute: DistributedComputeCoordinator):
  
  /**
   * VM statistics endpoint
   */
  val vmStats: ServerEndpoint[Any, Identity] =
    InfernoAPI.vmStatsEndpoint.serverLogicSuccess { _ =>
      vm.getStats
    }
  
  /**
   * Deploy model endpoint
   */
  val deployModel: ServerEndpoint[Any, Identity] =
    InfernoAPI.deployModelEndpoint.serverLogicSuccess { request =>
      // Build the model based on request
      val model = request.modelType.toLowerCase match
        case "feedforward" =>
          NNBuilder.buildFeedForward(
            request.inputSize,
            request.hiddenSizes,
            request.outputSize
          )
        case "classifier" =>
          NNBuilder.buildClassifier(
            request.inputSize,
            request.hiddenSizes,
            request.outputSize
          )
        case _ =>
          NNBuilder.buildFeedForward(
            request.inputSize,
            request.hiddenSizes,
            request.outputSize
          )
      
      // Deploy to VM
      val deploymentId = vm.deployModel(request.name, model)
      
      DeploymentResult(
        deploymentId = deploymentId,
        success = true,
        message = s"Model ${request.name} deployed successfully"
      )
    }
  
  /**
   * Execute model endpoint
   */
  val executeModel: ServerEndpoint[Any, Identity] =
    InfernoAPI.executeModelEndpoint.serverLogic { case (deploymentId, request) =>
      // Create input tensor
      val input = Tensor(
        TensorShape.vector(request.data.length),
        request.data.toArray
      )
      
      // Execute on VM
      vm.execute(deploymentId, input) match
        case Some(result) =>
          Right(InferenceResponse(
            success = true,
            result = Some(result.data.toList),
            error = None
          ))
        case None =>
          Left(s"Deployment $deploymentId not found or execution failed")
    }
  
  /**
   * List deployments endpoint
   */
  val listDeployments: ServerEndpoint[Any, Identity] =
    InfernoAPI.listDeploymentsEndpoint.serverLogicSuccess { _ =>
      vm.listDeployments.map { deployment =>
        DeploymentInfo(
          id = deployment.id,
          name = deployment.name,
          deploymentType = deployment.deploymentType.toString,
          status = deployment.process.state.toString,
          metadata = deployment.metadata
        )
      }
    }
  
  /**
   * Terminate deployment endpoint
   */
  val terminateDeployment: ServerEndpoint[Any, Identity] =
    InfernoAPI.terminateDeploymentEndpoint.serverLogicSuccess { deploymentId =>
      val success = vm.terminateDeployment(deploymentId)
      TerminationResult(
        success = success,
        message = if success then s"Deployment $deploymentId terminated" else s"Deployment $deploymentId not found"
      )
    }
  
  /**
   * Compute statistics endpoint
   */
  val computeStats: ServerEndpoint[Any, Identity] =
    InfernoAPI.computeStatsEndpoint.serverLogicSuccess { _ =>
      compute.getStats
    }
  
  /**
   * Register node endpoint
   */
  val registerNode: ServerEndpoint[Any, Identity] =
    InfernoAPI.registerNodeEndpoint.serverLogicSuccess { node =>
      compute.registerNode(node)
      NodeRegistrationResult(
        success = true,
        nodeId = node.id,
        message = s"Node ${node.id} registered successfully"
      )
    }
  
  /**
   * Submit task endpoint
   */
  val submitTask: ServerEndpoint[Any, Identity] =
    InfernoAPI.submitTaskEndpoint.serverLogicSuccess { request =>
      val taskType = request.taskType.toLowerCase match
        case "inference" => TaskType.ModelInference
        case "tensor" => TaskType.TensorComputation
        case "data" => TaskType.DataProcessing
        case _ => TaskType.DataProcessing
      
      val payload = request.data.map(d => DataPayload(d.toArray)).getOrElse(DataPayload(Array.empty))
      
      val taskId = compute.submitTask(request.name, taskType, payload)
      
      TaskSubmissionResult(
        taskId = taskId,
        status = "submitted"
      )
    }
  
  /**
   * Get task result endpoint
   */
  val getTaskResult: ServerEndpoint[Any, Identity] =
    InfernoAPI.getTaskResultEndpoint.serverLogic { taskId =>
      // Try to execute the task first if it's assigned but not running
      compute.getTaskStatus(taskId) match
        case Some(TaskStatus.Assigned) =>
          compute.executeTask(taskId)
        case _ => ()
      
      // Get the result
      compute.getTaskResult(taskId) match
        case Some(result) => Right(result)
        case None => Left(s"Task $taskId not found")
    }
  
  /**
   * Build model endpoint
   */
  val buildModel: ServerEndpoint[Any, Identity] =
    InfernoAPI.buildModelEndpoint.serverLogicSuccess { request =>
      val model = request.modelType.toLowerCase match
        case "feedforward" =>
          NNBuilder.buildFeedForward(
            request.inputSize,
            request.hiddenSizes,
            request.outputSize
          )
        case "classifier" =>
          NNBuilder.buildClassifier(
            request.inputSize,
            request.hiddenSizes,
            request.outputSize
          )
        case "perceptron" =>
          NNArchitectures.perceptron(request.inputSize)
        case "mlp" =>
          NNArchitectures.mlp(request.inputSize, request.outputSize)
        case "deepnet" =>
          NNArchitectures.deepNet(request.inputSize, request.outputSize)
        case _ =>
          NNBuilder.buildFeedForward(
            request.inputSize,
            request.hiddenSizes,
            request.outputSize
          )
      
      ModelBuildResponse(
        success = true,
        metadata = Some(model.metadata),
        architecture = Some(model.metadata.architecture),
        parameterCount = Some(model.parameterCount)
      )
    }
  
  /**
   * All endpoints
   */
  def allEndpoints: List[ServerEndpoint[Any, Identity]] =
    List(
      vmStats,
      deployModel,
      executeModel,
      listDeployments,
      terminateDeployment,
      computeStats,
      registerNode,
      submitTask,
      getTaskResult,
      buildModel
    )
