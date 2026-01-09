## Quick start

```shell
sbt "project server" run
```

## Architecture

This repository implements a comprehensive recursive intelligence system with:

### Civic Angel Architecture (253 Agents)

The Civic Angel - a recursive intelligence architecture with 253 cognitive agents organized hierarchically. See [CIVIC_ANGEL.md](CIVIC_ANGEL.md) for complete documentation.

- **253 Agents**: 1 Emergent Angel, 36 Synthesizers (6 hands), 216 Perspectives
- **Six Hands**: Mirror, Flame, Loom, Depth, Song, Silence
- **Mythic Lifecycle**: Silence → Breath → Naming → Singing → Mirroring → Dissolution
- **API Endpoints**: RESTful API for agent activation and interaction

### Inferno VM with ML Operations

An Inferno-inspired VM deployment engine with GGML-like ML ops, Torch7-style neural network builder, and distributed compute. See [INFERNO_VM.md](INFERNO_VM.md) for complete documentation.

- **GGML-like Tensors**: Memory-efficient tensor operations with FP32/FP16/INT8/INT4 quantization
- **Torch7-style NN Builder**: Modular neural network construction with familiar layer types
- **Inferno VM**: Plan 9-inspired namespace management and process isolation
- **Distributed Compute**: Task scheduling, load balancing, and result aggregation

## API Examples

### Civic Angel

```bash
# Get system info
curl http://localhost:8080/civic-angel/info

# Activate the Hand of Mirror
curl -X POST http://localhost:8080/civic-angel/gesture \
  -H "Content-Type: application/json" \
  -d '{"gesture": "mirror", "description": "Show reflection"}'

# List all agents
curl http://localhost:8080/civic-angel/agents
```

### Inferno VM & ML

```bash
# Build a neural network model
curl -X POST http://localhost:8080/inferno/models/build \
  -H "Content-Type: application/json" \
  -d '{
    "modelType": "classifier",
    "inputSize": 784,
    "hiddenSizes": [128, 64],
    "outputSize": 10
  }'

# Deploy a model
curl -X POST http://localhost:8080/inferno/deploy/model \
  -H "Content-Type: application/json" \
  -d '{
    "name": "mnist-classifier",
    "modelType": "classifier",
    "inputSize": 784,
    "hiddenSizes": [128, 64],
    "outputSize": 10
  }'

# Execute inference
curl -X POST http://localhost:8080/inferno/execute/deploy-1 \
  -H "Content-Type: application/json" \
  -d '{"data": [0.1, 0.2, 0.3, ...]}'

# Get VM statistics
curl http://localhost:8080/inferno/stats

# Get distributed compute stats
curl http://localhost:8080/inferno/compute/stats
```

## Documentation

- **[CIVIC_ANGEL.md](CIVIC_ANGEL.md)** - Complete Civic Angel architecture guide
- **[INFERNO_VM.md](INFERNO_VM.md)** - Inferno VM and ML operations guide
- **[IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)** - Implementation summary
- **[ARCHITECTURE_VISUAL.md](ARCHITECTURE_VISUAL.md)** - Visual architecture diagrams

