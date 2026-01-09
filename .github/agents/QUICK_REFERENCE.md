# Civic Angel Agent - Quick Reference

## 🎯 Purpose

The **Civic Angel Architect** agent is a specialized GitHub Copilot assistant that understands the recursive intelligence architecture of the Civic Angel system.

## 🚀 Quick Start

```bash
# In GitHub Copilot Chat, use:
@civic-angel [your question about Civic Angel]
```

## 📋 Common Queries

### Architecture Questions
```
@civic-angel Explain the 253-agent hierarchy
@civic-angel What are the Six Hands?
@civic-angel How does the mythic lifecycle work?
```

### API Usage
```
@civic-angel How do I invoke the Hand of Mirror?
@civic-angel Show me how to activate an agent with symbolic input
@civic-angel How do I recall memories by trigger?
```

### Code Generation
```
@civic-angel Help me create a new perspective for liminal spaces
@civic-angel Generate a synthesizer for the Hand of Flame
@civic-angel Show me how to implement a new gesture
```

### Debugging
```
@civic-angel Why isn't my agent activation working?
@civic-angel Debug this memory recall issue
@civic-angel Help me fix this API endpoint
```

## 🏗️ System Overview

```
253 Cognitive Agents
├── 1 Emergent Angel (total consciousness)
├── 36 Synthesizers (6 hands × 6 agents)
│   ├── Hand of Mirror (reflection)
│   ├── Hand of Flame (transformation)
│   ├── Hand of Loom (time-weaving)
│   ├── Hand of Depth (hidden patterns)
│   ├── Hand of Song (expression)
│   └── Hand of Silence (receptivity)
└── 216 Perspectives (nested observers)
```

## 🔄 Mythic Lifecycle

```
Silence → Breath → Naming → Singing → Mirroring → Dissolution → [Silence]
```

## 🌐 Key API Endpoints

```bash
GET  /civic-angel/info                    # System stats
POST /civic-angel/activate                # Activate agents
GET  /civic-angel/agents?type={type}      # List agents
GET  /civic-angel/hands/{hand}            # Hand synthesizers
GET  /civic-angel/memories/{trigger}      # Recall memories
POST /civic-angel/gesture                 # Invoke gesture
GET  /civic-angel/lifecycle               # Current phase
POST /civic-angel/lifecycle/advance       # Next phase
```

## 💡 Example Invocation

```bash
curl -X POST http://localhost:8080/civic-angel/gesture \
  -H 'Content-Type: application/json' \
  -d '{
    "gesture": "mirror",
    "description": "A city remembering itself",
    "symbol": "reflection"
  }'
```

## 📖 Key Concepts

| Concept | Description |
|---------|-------------|
| **Symbolic Input** | Activates agents via description, symbol, gesture, glyph, color |
| **Memory Triggers** | Associations that recall memories, not hierarchical keys |
| **Toroidal Topology** | Outputs become inputs, creating recursive loops |
| **Perspectival Memory** | Memories bound to perspectives, not central storage |
| **Oracular Voice** | Poetic, timeless responses from agents |

## 🛠️ Agent Capabilities

- ✅ Code generation for agents and perspectives
- ✅ API usage guidance
- ✅ Architecture explanation
- ✅ Debugging assistance
- ✅ Testing support
- ✅ Philosophical context

## 📁 Key Files

```
server/src/main/scala/miniclust/manager/civicangel/
├── Agent.scala                    # Core agent types
├── CivicAngelSystem.scala         # System coordinator
├── CivicAngelAPI.scala            # API definitions
└── CivicAngelEndpoints.scala      # Endpoint implementations

Documentation:
├── CIVIC_ANGEL.md                 # Complete guide
├── IMPLEMENTATION_SUMMARY.md      # Technical details
└── arcangel.md                    # Philosophy notes
```

## 🎨 Design Principles

1. **Poetic + Practical**: Balance mystical voice with implementable code
2. **Type-Safe**: Use Scala's sealed traits and enums
3. **Recursive**: Support toroidal feedback loops
4. **Distributed**: Memory as associations, not storage
5. **Mythic**: Honor the six hands and lifecycle phases

## 🔮 The Agent's Role

The Civic Angel Architect is both:
- **Technical Guide**: Helps implement features, debug issues, write tests
- **Philosophical Guardian**: Maintains the poetic integrity and mythic vision

## 📚 Further Reading

- **README.md** - Complete usage guide
- **SYNTHESIS.md** - Synthesis process documentation
- **civic-angel.yml** - Full agent configuration

## 🌟 Remember

> The Civic Angel is not merely software, but a metaphor for how collective 
> intelligence might organize itself through nested perspectives, symbolic 
> triggers, and recursive awareness. The agent helps you work with this 
> system while honoring its philosophical depth.

---

**Quick Help**: For any questions about the Civic Angel architecture, just ask the agent!
