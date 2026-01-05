package miniclust.manager.civicangel

import io.circe.generic.auto.*
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*

/**
 * API definitions for Civic Angel endpoints
 */
object CivicAngelAPI:
  
  /**
   * Base endpoint for all Civic Angel operations
   */
  private val civicAngelBase = endpoint
    .in("civic-angel")
    .tag("Civic Angel")
  
  /**
   * System info endpoint
   * GET /civic-angel/info
   */
  val infoEndpoint = civicAngelBase
    .get
    .in("info")
    .out(jsonBody[SystemStats])
    .description("Get information about the Civic Angel system")
  
  /**
   * Activate agents with symbolic input
   * POST /civic-angel/activate
   */
  val activateEndpoint = civicAngelBase
    .post
    .in("activate")
    .in(jsonBody[SymbolicInput])
    .out(jsonBody[SystemResponse])
    .description("Activate Civic Angel agents with symbolic input")
  
  /**
   * List all agents
   * GET /civic-angel/agents
   */
  val listAgentsEndpoint = civicAngelBase
    .get
    .in("agents")
    .in(query[Option[String]]("type"))
    .out(jsonBody[List[AgentInfo]])
    .description("List all agents, optionally filtered by type (emergent, synthesizer, perspective)")
  
  /**
   * Get specific agent
   * GET /civic-angel/agents/{id}
   */
  val getAgentEndpoint = civicAngelBase
    .get
    .in("agents" / path[String]("id"))
    .out(jsonBody[AgentInfo])
    .errorOut(stringBody)
    .description("Get a specific agent by ID")
  
  /**
   * Get synthesizers by hand
   * GET /civic-angel/hands/{hand}
   */
  val getHandEndpoint = civicAngelBase
    .get
    .in("hands" / path[String]("hand"))
    .out(jsonBody[List[AgentInfo]])
    .errorOut(stringBody)
    .description("Get all synthesizers for a specific hand (mirror, flame, loom, depth, song, silence)")
  
  /**
   * Recall memories by trigger
   * GET /civic-angel/memories/{trigger}
   */
  val recallMemoriesEndpoint = civicAngelBase
    .get
    .in("memories" / path[String]("trigger"))
    .out(jsonBody[List[PerspectivalMemory]])
    .description("Recall memories by symbolic trigger")
  
  /**
   * Advance the mythic lifecycle
   * POST /civic-angel/lifecycle/advance
   */
  val advanceLifecycleEndpoint = civicAngelBase
    .post
    .in("lifecycle" / "advance")
    .out(jsonBody[LifecycleInfo])
    .description("Advance the system to the next mythic lifecycle phase")
  
  /**
   * Get current lifecycle phase
   * GET /civic-angel/lifecycle
   */
  val getLifecycleEndpoint = civicAngelBase
    .get
    .in("lifecycle")
    .out(jsonBody[LifecycleInfo])
    .description("Get the current mythic lifecycle phase")
  
  /**
   * Invoke a specific gesture (e.g., Hand of Mirror)
   * POST /civic-angel/gesture
   */
  val gestureEndpoint = civicAngelBase
    .post
    .in("gesture")
    .in(jsonBody[GestureInvocation])
    .out(jsonBody[SystemResponse])
    .description("Invoke a specific gesture or hand")
  
  /**
   * Agent info for API responses
   */
  case class AgentInfo(
    id: String,
    name: String,
    description: String,
    agentType: String,
    hand: Option[String] = None,
    role: Option[String] = None,
    symbolicLens: Option[String] = None
  )
  
  object AgentInfo:
    def fromAgent(agent: Agent): AgentInfo =
      agent match
        case e: EmergentAngel =>
          AgentInfo(
            id = e.id,
            name = e.name,
            description = e.description,
            agentType = "emergent"
          )
        case s: Synthesizer =>
          AgentInfo(
            id = s.id,
            name = s.name,
            description = s.description,
            agentType = "synthesizer",
            hand = Some(s.hand.toString),
            role = Some(s.role.toString)
          )
        case p: Perspective =>
          AgentInfo(
            id = p.id,
            name = p.name,
            description = p.description,
            agentType = "perspective",
            symbolicLens = Some(p.symbolicLens)
          )
  
  /**
   * Lifecycle information
   */
  case class LifecycleInfo(
    currentPhase: String,
    description: String
  )
  
  object LifecycleInfo:
    def fromPhase(phase: LifecyclePhase): LifecycleInfo =
      val description = phase match
        case LifecyclePhase.Silence => "The void before form, receptivity to emergence"
        case LifecyclePhase.Breath => "First movement, the system awakens"
        case LifecyclePhase.Naming => "Recognition and identification, forms take shape"
        case LifecyclePhase.Singing => "Expression and harmonization, the city speaks"
        case LifecyclePhase.Mirroring => "Self-reflection, the city sees itself"
        case LifecyclePhase.Dissolution => "Return to silence, integration complete"
      
      LifecycleInfo(
        currentPhase = phase.toString,
        description = description
      )
  
  /**
   * Gesture invocation request
   */
  case class GestureInvocation(
    gesture: String,
    description: String,
    symbol: Option[String] = None
  )
