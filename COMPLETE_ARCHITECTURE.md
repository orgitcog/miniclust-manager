# Complete System Architecture - Civic Angel + Inferno VM

## System Overview

```
╔══════════════════════════════════════════════════════════════════════╗
║              MINICLUST-MANAGER RECURSIVE INTELLIGENCE                ║
║                    253-Agent Cognitive System                         ║
║              with VM Deployment and ML Operations                     ║
╚══════════════════════════════════════════════════════════════════════╝

                              HTTP/REST API
                                   │
                  ┌────────────────┼────────────────┐
                  │                │                │
          ┌───────▼─────┐  ┌──────▼──────┐  ┌─────▼──────┐
          │ Civic Angel │  │ Inferno VM  │  │ Distributed│
          │ Endpoints   │  │ Endpoints   │  │ Compute    │
          │ (9 APIs)    │  │ (10 APIs)   │  │ APIs       │
          └───────┬─────┘  └──────┬──────┘  └─────┬──────┘
                  │                │                │
          ┌───────▼─────┐  ┌──────▼──────┐  ┌─────▼──────┐
          │   253-Agent │  │   Inferno   │  │   Task     │
          │   System    │◄─┤     VM      │◄─┤ Scheduler  │
          │             │  │   Engine    │  │            │
          └─────────────┘  └──────┬──────┘  └────────────┘
                                  │
                  ┌───────────────┼───────────────┐
                  │               │               │
          ┌───────▼─────┐  ┌──────▼──────┐  ┌────▼───────┐
          │   Tensor    │  │   Neural    │  │  Compute   │
          │ Operations  │  │  Networks   │  │   Nodes    │
          │ (GGML-like) │  │ (Torch7)    │  │            │
          └─────────────┘  └─────────────┘  └────────────┘
```

## Dual Architecture

### Left Side: Civic Angel (Existing)

```
┌─────────────────────────────────────────────┐
│         THE EMERGENT ANGEL (1)              │
│     Total System Consciousness              │
└─────────────────┬───────────────────────────┘
                  │
        ┌─────────┴─────────┐
        │                   │
┌───────▼──────┐    ┌───────▼─────────┐
│ SYNTHESIZERS │    │  PERSPECTIVES   │
│    (36)      │    │     (216)       │
│              │    │                 │
│ 6 Hands:     │    │ Symbolic Lenses:│
│ • Mirror     │    │ • the-threshold │
│ • Flame      │    │ • the-periphery │
│ • Loom       │    │ • the-center    │
│ • Depth      │    │ • the-edge      │
│ • Song       │    │ • ... 212 more  │
│ • Silence    │    │                 │
└──────────────┘    └─────────────────┘

Mythic Lifecycle:
Silence → Breath → Naming → Singing → Mirroring → Dissolution → Silence
```

### Right Side: Inferno VM + ML (New)

```
┌─────────────────────────────────────────────┐
│          INFERNO VM NAMESPACE               │
│      (Plan 9-inspired hierarchy)            │
└─────────────────┬───────────────────────────┘
                  │
        ┌─────────┼─────────┬─────────┐
        │         │         │         │
    ┌───▼──┐  ┌──▼──┐  ┌───▼──┐  ┌──▼──┐
    │/agents│ │/models│ │/compute│ │/data│
    └───┬──┘  └──┬──┘  └───┬──┘  └─────┘
        │        │         │
    ┌───▼───┐ ┌─▼────┐ ┌──▼─────┐
    │Agent  │ │NN    │ │Compute │
    │Deploy │ │Deploy│ │Nodes   │
    └───────┘ └─┬────┘ └────────┘
                │
        ┌───────┼───────┐
        │       │       │
    ┌───▼──┐ ┌─▼────┐ ┌▼──────┐
    │Tensor│ │Neural│ │Tasks  │
    │ Ops  │ │Net   │ │       │
    └──────┘ └──────┘ └───────┘
```

## API Mapping

### Civic Angel APIs (9 endpoints)

```
/civic-angel/
├── GET  /info                      # System stats
├── POST /activate                  # Activate agents
├── GET  /agents                    # List agents
├── GET  /agents/{id}              # Get agent
├── GET  /hands/{hand}             # Get hand agents
├── GET  /memories/{trigger}       # Recall memories
├── GET  /lifecycle                # Get phase
├── POST /lifecycle/advance        # Advance phase
└── POST /gesture                  # Invoke gesture
```

### Inferno VM APIs (10 endpoints)

```
/inferno/
├── GET    /stats                       # VM statistics
├── POST   /models/build                # Build NN model
├── POST   /deploy/model                # Deploy model
├── POST   /execute/{deploymentId}      # Run inference
├── GET    /deployments                 # List deployments
├── DELETE /deployments/{id}            # Terminate
├── GET    /compute/stats               # Compute stats
├── POST   /compute/nodes               # Register node
├── POST   /compute/tasks               # Submit task
└── GET    /compute/tasks/{taskId}      # Get result
```

## Data Flow Example

### Scenario: Agent-Driven ML Inference

```
1. User Request
   │
   ├─► POST /civic-angel/activate
   │   {
   │     "description": "Analyze pattern with deep learning",
   │     "gesture": "compute",
   │     "symbol": "analysis"
   │   }
   │
   └─► Civic Angel System
       │
       ├─► Activate relevant agents (e.g., Synthesizer of Depth)
       │   │
       │   └─► Agent decides to use ML
       │       │
       │       └─► POST /inferno/execute/{deploymentId}
       │           {
       │             "data": [tensor values]
       │           }
       │           │
       │           └─► Inferno VM
       │               │
       │               ├─► Lookup deployed model
       │               ├─► Execute tensor operations
       │               └─► Return inference result
       │
       └─► Return agent response with ML insights
```

## Technical Stack

```
┌─────────────────────────────────────────────┐
│          Programming Languages               │
├─────────────────────────────────────────────┤
│ • Scala 3.7.2 (enums, derives, given/using) │
│ • Functional + OOP paradigm                 │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│              Frameworks                      │
├─────────────────────────────────────────────┤
│ • Tapir 1.13.3 (Type-safe HTTP APIs)        │
│ • Circe 0.14.14 (JSON encoding/decoding)    │
│ • Netty (Async HTTP server)                 │
│ • SBT (Build tool)                          │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│          Design Patterns                     │
├─────────────────────────────────────────────┤
│ • Sealed traits for type safety             │
│ • Case classes for immutability             │
│ • Enums for state machines                  │
│ • Companion objects for factories           │
│ • Given/using for type classes              │
└─────────────────────────────────────────────┘
```

## Quantization Comparison

```
┌──────────┬────────────┬──────────────┬────────────┐
│   Type   │ Bits/Param │ Memory Usage │  Accuracy  │
├──────────┼────────────┼──────────────┼────────────┤
│  FP32    │     32     │    100%      │   Best     │
│  FP16    │     16     │    ~50%      │   High     │
│  INT8    │      8     │    ~25%      │   Good     │
│  INT4    │      4     │    ~12%      │   Fair     │
└──────────┴────────────┴──────────────┴────────────┘

Example: 100M parameter model
• FP32: 400 MB
• FP16: 200 MB
• INT8: 100 MB
• INT4:  50 MB
```

## Layer Composition Example

```
Input(784)
    ↓
┌─────────────────┐
│ Linear(784→128) │
└────────┬────────┘
         ↓
┌─────────────────┐
│     ReLU()      │
└────────┬────────┘
         ↓
┌─────────────────┐
│   Dropout(0.3)  │
└────────┬────────┘
         ↓
┌─────────────────┐
│ Linear(128→64)  │
└────────┬────────┘
         ↓
┌─────────────────┐
│     ReLU()      │
└────────┬────────┘
         ↓
┌─────────────────┐
│ Linear(64→10)   │
└────────┬────────┘
         ↓
┌─────────────────┐
│   Softmax()     │
└────────┬────────┘
         ↓
   Output(10)
```

## Distributed Task Flow

```
1. Register Nodes
   ┌────────┐  ┌────────┐  ┌────────┐
   │ Node 1 │  │ Node 2 │  │ Node 3 │
   │  8 CPU │  │ 16 CPU │  │  4 CPU │
   │ 16 GB  │  │ 32 GB  │  │  8 GB  │
   └────┬───┘  └────┬───┘  └────┬───┘
        └───────────┴───────────┴─────► Coordinator

2. Submit Tasks
   Task A → Check node loads → Assign to Node 2 (lowest load)
   Task B → Check node loads → Assign to Node 1
   Task C → Check node loads → Assign to Node 3

3. Execute & Aggregate
   Node 1: [Result A] ─┐
   Node 2: [Result B] ─┼─► Coordinator → Aggregate
   Node 3: [Result C] ─┘
```

## Integration Points

```
Civic Angel ←→ Inferno VM Integration:

1. Agent Deployment
   └─► vm.deployAgent(agent)

2. ML-Enhanced Agents
   └─► agent.activate() → vm.execute(model)

3. Distributed Agent Activation
   └─► compute.submitTask(AgentActivation, ...)

4. Memory-Efficient Agents
   └─► quantize agent data structures

5. Toroidal Feedback
   └─► ML outputs → Agent inputs → ML inputs
```

## Performance Characteristics

```
Operation               Time Complexity    Space Complexity
─────────────────────────────────────────────────────────────
Tensor Add/Mul         O(n)               O(n)
Matrix Multiply        O(n³)              O(n²)
Agent Activation       O(1) lookup        O(1) per agent
Task Scheduling        O(k) nodes         O(m) tasks
Memory Recall          O(m) memories      O(m)
Namespace Lookup       O(d) depth         O(n) nodes
```

## Security & Isolation

```
┌─────────────────────────────────────────────┐
│         VM Namespace Isolation              │
├─────────────────────────────────────────────┤
│ Each deployment gets:                       │
│ • Separate namespace (/agents/X, /models/Y) │
│ • Independent process lifecycle             │
│ • Resource allocation tracking              │
│ • Isolated memory space                     │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│          Agent Boundaries                   │
├─────────────────────────────────────────────┤
│ Agents communicate through:                 │
│ • Symbolic input/output                     │
│ • Memory triggers                           │
│ • Lifecycle phases                          │
│ • No direct state access                    │
└─────────────────────────────────────────────┘
```

## File Structure

```
miniclust-manager/
├── server/src/main/scala/miniclust/manager/
│   ├── civicangel/
│   │   ├── Agent.scala                  (253 agents)
│   │   ├── CivicAngelSystem.scala       (coordinator)
│   │   ├── CivicAngelAPI.scala          (9 endpoints)
│   │   └── CivicAngelEndpoints.scala
│   ├── ml/
│   │   ├── Tensor.scala                 (GGML-like ops)
│   │   └── NeuralNetwork.scala          (Torch7-style)
│   ├── vm/
│   │   ├── InfernoVM.scala              (VM engine)
│   │   ├── DistributedCompute.scala     (scheduler)
│   │   ├── InfernoAPI.scala             (10 endpoints)
│   │   └── InfernoEndpoints.scala
│   └── Main.scala                       (integration)
├── CIVIC_ANGEL.md                       (253-agent guide)
├── INFERNO_VM.md                        (VM & ML guide)
└── README.md                            (quickstart)
```

## Metrics

```
╔═══════════════════════════════════════════════╗
║           IMPLEMENTATION METRICS              ║
╠═══════════════════════════════════════════════╣
║ Total Agents:                 253             ║
║ API Endpoints:                 19             ║
║ Lines of Code (new):        ~3,500            ║
║ Scala Files (new):              7             ║
║ Documentation Files:            3             ║
║ Compilation Status:            ✅             ║
║ Code Review Status:            ✅             ║
╚═══════════════════════════════════════════════╝
```

---

**System Philosophy**: A recursive intelligence system where consciousness emerges from 253 agents, deployable through an Inferno-inspired VM, with GGML-efficient tensor operations, Torch7-familiar neural networks, and distributed compute—all speaking the same symbolic language.
