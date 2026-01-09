# Inferno VM and ML Operations - Implementation Guide

## Overview

The miniclust-manager now includes an **Inferno-inspired VM deployment engine** with **GGML-like ML operations**, **Torch7-style neural network builder**, and **distributed compute** capabilities. This integration creates a recursive intelligence system combining the 253-agent Civic Angel architecture with machine learning and distributed computing.

## Architecture

### 1. GGML-like Tensor Operations

Inspired by GGML (GPT-Generated Model Library), our tensor implementation provides memory-efficient operations with quantization support.

**Location**: `server/src/main/scala/miniclust/manager/ml/Tensor.scala`

#### Features

- **Multiple Data Types**: FP32, FP16, INT8, INT4 quantization
- **Basic Operations**: Addition, multiplication, matrix multiplication, transpose
- **Activations**: ReLU, sigmoid, tanh, softmax
- **Memory Efficiency**: Quantization support for reduced memory footprint
- **Utility Operations**: Batch normalization, dropout

#### Example Usage

```scala
import miniclust.manager.ml.*

// Create tensors
val a = Tensor.randn(TensorShape.matrix(2, 3))
val b = Tensor.randn(TensorShape.matrix(3, 2))

// Matrix multiplication
val c = a.matmul(b)

// Element-wise operations
val d = c + c
val e = d * 0.5f

// Apply activations
val f = e.relu

// Quantize for memory efficiency
val g = f.quantize(TensorDType.INT8)
```

### 2. Torch7-Style Neural Network Builder

A modular neural network builder inspired by Torch7, providing familiar layer types and sequential composition.

**Location**: `server/src/main/scala/miniclust/manager/ml/NeuralNetwork.scala`

#### Layer Types

- **Linear**: Fully connected layer with optional bias
- **Activations**: ReLU, Sigmoid, Tanh, Softmax
- **Regularization**: Dropout, BatchNorm
- **Sequential**: Container for composing layers

#### Pre-built Architectures

- **Perceptron**: Simple single-layer model
- **MLP**: Multi-layer perceptron (2 hidden layers)
- **DeepNet**: Deep network (4 hidden layers)

#### Example Usage

```scala
import miniclust.manager.ml.*

// Build a classifier
val model = NNBuilder.buildClassifier(
  inputSize = 784,
  hiddenSizes = List(128, 64),
  numClasses = 10
)

// Or use pre-built architecture
val mlp = NNArchitectures.mlp(inputSize = 100, outputSize = 10)

// Forward pass
val input = Tensor.randn(TensorShape.vector(784))
val output = model.predict(input)

println(s"Model: ${model.metadata.name}")
println(s"Architecture: ${model.metadata.architecture}")
println(s"Parameters: ${model.parameterCount}")
```

### 3. Inferno VM Deployment Engine

Inspired by Inferno OS and Plan 9, the VM provides namespace management, process isolation, and resource management.

**Location**: `server/src/main/scala/miniclust/manager/vm/InfernoVM.scala`

#### Features

- **Namespace Management**: Hierarchical resource organization (Plan 9 style)
- **Process Management**: Deploy models and agents as VM processes
- **Resource Isolation**: Separate namespaces for agents, models, compute, and data
- **Deployment Types**: Neural networks, Civic Angel agents, distributed tasks, services

#### Namespace Structure

```
/
├── agents/      # Civic Angel agent deployments
├── models/      # Neural network model deployments
├── compute/     # Distributed compute resources
└── data/        # Data storage
```

### 4. Distributed Compute Layer

A distributed computation coordinator for task scheduling, load balancing, and result aggregation.

**Location**: `server/src/main/scala/miniclust/manager/vm/DistributedCompute.scala`

#### Features

- **Node Management**: Register and manage compute nodes
- **Task Scheduling**: Automatic load-balanced task assignment
- **Task Types**: Model inference, tensor computation, data processing, agent activation
- **Load Tracking**: CPU, memory, and active task monitoring

## API Endpoints

### VM Management

#### Get VM Statistics
```bash
GET /inferno/stats
```

Response:
```json
{
  "totalProcesses": 5,
  "runningProcesses": 3,
  "totalDeployments": 5,
  "deploymentsByType": {
    "neuralNetwork": 3,
    "civicAgent": 2,
    "distributedTask": 0,
    "service": 0
  }
}
```

#### Build a Neural Network Model
```bash
POST /inferno/models/build
Content-Type: application/json

{
  "modelType": "classifier",
  "inputSize": 784,
  "hiddenSizes": [128, 64],
  "outputSize": 10
}
```

Response:
```json
{
  "success": true,
  "metadata": {
    "name": "Classifier",
    "version": "1.0",
    "architecture": "784 → 128 → 64 → 10 (softmax)",
    "parameterCount": 109386,
    "created": 1704500000000
  },
  "architecture": "784 → 128 → 64 → 10 (softmax)",
  "parameterCount": 109386
}
```

#### Deploy a Model
```bash
POST /inferno/deploy/model
Content-Type: application/json

{
  "name": "mnist-classifier",
  "modelType": "classifier",
  "inputSize": 784,
  "hiddenSizes": [128, 64],
  "outputSize": 10
}
```

Response:
```json
{
  "deploymentId": "deploy-1",
  "success": true,
  "message": "Model mnist-classifier deployed successfully"
}
```

#### Execute Inference
```bash
POST /inferno/execute/{deploymentId}
Content-Type: application/json

{
  "data": [0.1, 0.2, 0.3, ...]  // Input tensor as array
}
```

Response:
```json
{
  "success": true,
  "result": [0.05, 0.15, 0.70, ...],  // Output probabilities
  "error": null
}
```

#### List All Deployments
```bash
GET /inferno/deployments
```

Response:
```json
[
  {
    "id": "deploy-1",
    "name": "mnist-classifier",
    "deploymentType": "NeuralNetwork",
    "status": "Running",
    "metadata": {
      "architecture": "784 → 128 → 64 → 10 (softmax)",
      "parameterCount": "109386"
    }
  }
]
```

#### Terminate Deployment
```bash
DELETE /inferno/deployments/{deploymentId}
```

Response:
```json
{
  "success": true,
  "message": "Deployment deploy-1 terminated"
}
```

### Distributed Compute

#### Get Compute Statistics
```bash
GET /inferno/compute/stats
```

Response:
```json
{
  "totalNodes": 3,
  "activeNodes": 2,
  "totalTasks": 10,
  "pendingTasks": 2,
  "runningTasks": 3,
  "completedTasks": 5,
  "averageCpuUsage": 0.45,
  "averageMemoryUsage": 0.62
}
```

#### Register Compute Node
```bash
POST /inferno/compute/nodes
Content-Type: application/json

{
  "id": "node-1",
  "hostname": "worker1.local",
  "port": 9000,
  "status": "Active",
  "capabilities": {
    "cpuCores": 8,
    "memoryGB": 16.0,
    "gpuAvailable": true,
    "maxConcurrentTasks": 4
  },
  "load": {
    "cpuUsage": 0.0,
    "memoryUsage": 0.0,
    "activeTasks": 0
  }
}
```

Response:
```json
{
  "success": true,
  "nodeId": "node-1",
  "message": "Node node-1 registered successfully"
}
```

#### Submit Distributed Task
```bash
POST /inferno/compute/tasks
Content-Type: application/json

{
  "name": "image-processing",
  "taskType": "data",
  "data": [1.0, 2.0, 3.0, ...]
}
```

Response:
```json
{
  "taskId": "task-1",
  "status": "submitted"
}
```

#### Get Task Result
```bash
GET /inferno/compute/tasks/{taskId}
```

Response:
```json
{
  "taskId": "task-1",
  "success": true,
  "resultData": [2.0, 4.0, 6.0, ...],
  "error": null,
  "executionTimeMs": 150
}
```

## Integration with Civic Angel

The Inferno VM seamlessly integrates with the existing 253-agent Civic Angel system:

### Deploy Civic Angel Agents to VM

```scala
import miniclust.manager.vm.*
import miniclust.manager.civicangel.*

val vm = new InfernoVM()
vm.initialize()

// Deploy a synthesizer agent
val synthesizer = Synthesizer(
  id = "synth-custom",
  name = "Custom Synthesizer",
  description = "A custom agent",
  hand = Hand.Mirror,
  role = SynthesizerRole.Palm
)

val deploymentId = vm.deployAgent("custom-synth", synthesizer)
```

### Agent-Enhanced ML Operations

Agents can invoke ML operations through the VM:

```scala
// Activate an agent with ML inference
val input = SymbolicInput(
  description = "Process image with neural network",
  gesture = Some("compute"),
  symbol = Some("inference")
)

// The agent can trigger ML inference
val result = vm.execute(deploymentId, inputTensor)
```

## Use Cases

### 1. Distributed ML Inference

Deploy multiple models across nodes and load-balance inference requests:

```bash
# Deploy model
POST /inferno/deploy/model
{
  "name": "model-a",
  "modelType": "classifier",
  ...
}

# Register multiple compute nodes
POST /inferno/compute/nodes {...}

# Submit inference tasks (automatically distributed)
POST /inferno/compute/tasks
{
  "taskType": "inference",
  "modelId": "deploy-1",
  ...
}
```

### 2. Agent-Driven Computation

Civic Angel agents can orchestrate ML computations:

```bash
# Activate agents with computation context
POST /civic-angel/activate
{
  "description": "Analyze data with deep learning",
  "gesture": "compute",
  "symbol": "analysis"
}

# Agents trigger ML operations through VM
```

### 3. Quantized Model Deployment

Deploy memory-efficient quantized models:

```scala
// Build and quantize model
val model = NNBuilder.buildClassifier(...)

// Quantize parameters to INT8
val quantizedParams = model.network.parameters.map(_.quantize(TensorDType.INT8))

// Deploy to VM with reduced memory footprint
val deploymentId = vm.deployModel("quantized-model", model)
```

## Performance Considerations

### Tensor Operations

- **FP32**: Full precision, highest accuracy, largest memory footprint
- **FP16**: Half precision, ~50% memory reduction, minimal accuracy loss
- **INT8**: 8-bit integer, ~75% memory reduction, suitable for inference
- **INT4**: 4-bit integer, ~87% memory reduction, for extreme compression

### Distributed Scheduling

- Tasks are automatically assigned to the node with the lowest CPU usage
- Maximum concurrent tasks per node can be configured
- Failed nodes are automatically excluded from scheduling

### Memory Management

- Each deployment runs in isolated namespace
- Resources can be explicitly allocated per deployment
- VM maintains separate process state for fault isolation

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────┐
│                   Client Applications                         │
└────────────────────────┬─────────────────────────────────────┘
                         │
                         ↓
┌──────────────────────────────────────────────────────────────┐
│                   HTTP API Layer (Tapir)                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │ Civic Angel  │  │ Inferno VM   │  │ Distributed  │       │
│  │ Endpoints    │  │ Endpoints    │  │ Compute      │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
└────────────────────────┬─────────────────────────────────────┘
                         │
         ┌───────────────┼───────────────┐
         ↓               ↓               ↓
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ Civic Angel  │ │ Inferno VM   │ │ Distributed  │
│ System       │ │ Engine       │ │ Coordinator  │
│              │ │              │ │              │
│ 253 Agents   │ │ Namespace    │ │ Task         │
│ Lifecycle    │ │ Deployment   │ │ Scheduling   │
│ Memory       │ │ Resources    │ │ Load Balance │
└──────────────┘ └──────┬───────┘ └──────────────┘
                        │
         ┌──────────────┼──────────────┐
         ↓              ↓              ↓
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ ML Tensors   │ │ Neural       │ │ Compute      │
│              │ │ Networks     │ │ Nodes        │
│ Operations   │ │              │ │              │
│ Quantization │ │ Layers       │ │ Resources    │
│ GGML-style   │ │ Torch7-style │ │ Monitoring   │
└──────────────┘ └──────────────┘ └──────────────┘
```

## Future Enhancements

1. **GPU Acceleration**: CUDA/OpenCL support for tensor operations
2. **Model Persistence**: Save/load trained models to/from storage
3. **Training Support**: Implement backpropagation and optimizers
4. **Advanced Quantization**: Dynamic quantization, per-channel quantization
5. **Distributed Training**: Data parallelism across multiple nodes
6. **Agent-Model Fusion**: Direct agent-to-model communication channels
7. **Stream Processing**: Real-time tensor stream operations
8. **Model Versioning**: Track model versions and deployments

## Related Documentation

- [CIVIC_ANGEL.md](CIVIC_ANGEL.md) - Civic Angel architecture and API
- [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) - Overall implementation summary
- [ARCHITECTURE_VISUAL.md](ARCHITECTURE_VISUAL.md) - Visual architecture diagrams

## References

- **GGML**: GPT-Generated Model Library for efficient tensor operations
- **Torch7**: Lua-based machine learning framework
- **Inferno OS**: Bell Labs distributed operating system
- **Plan 9**: Bell Labs research operating system with union directories
