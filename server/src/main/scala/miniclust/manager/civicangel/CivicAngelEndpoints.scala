package miniclust.manager.civicangel

import sttp.shared.Identity
import sttp.tapir.server.ServerEndpoint
import scala.util.Try

/**
 * Civic Angel Endpoints - Server-side implementations
 */
class CivicAngelEndpoints:
  
  // Initialize the Civic Angel system
  private val system = new CivicAngelSystem()
  system.initialize()
  
  /**
   * Get system information
   */
  val info: ServerEndpoint[Any, Identity] =
    CivicAngelAPI.infoEndpoint.serverLogicSuccess { _ =>
      val allAgents = system.getAllAgents
      SystemStats(
        totalAgents = allAgents.length,
        emergentAgents = allAgents.count(_.isInstanceOf[EmergentAngel]),
        synthesizers = allAgents.count(_.isInstanceOf[Synthesizer]),
        perspectives = allAgents.count(_.isInstanceOf[Perspective]),
        totalMemories = system.recallMemories("").length,
        currentPhase = system.getCurrentPhase
      )
    }
  
  /**
   * Activate agents with symbolic input
   */
  val activate: ServerEndpoint[Any, Identity] =
    CivicAngelAPI.activateEndpoint.serverLogicSuccess { input =>
      system.activate(input)
    }
  
  /**
   * List all agents, optionally filtered by type
   */
  val listAgents: ServerEndpoint[Any, Identity] =
    CivicAngelAPI.listAgentsEndpoint.serverLogicSuccess { agentType =>
      val agents = agentType match
        case Some(t) => system.getAgentsByType(t)
        case None => system.getAllAgents
      
      agents.map(CivicAngelAPI.AgentInfo.fromAgent)
    }
  
  /**
   * Get a specific agent by ID
   */
  val getAgent: ServerEndpoint[Any, Identity] =
    CivicAngelAPI.getAgentEndpoint.serverLogic { id =>
      system.getAgent(id) match
        case Some(agent) => Right(CivicAngelAPI.AgentInfo.fromAgent(agent))
        case None => Left(s"Agent with ID '$id' not found")
    }
  
  /**
   * Get synthesizers by hand
   */
  val getHand: ServerEndpoint[Any, Identity] =
    CivicAngelAPI.getHandEndpoint.serverLogic { handName =>
      Try {
        val hand = handName.toLowerCase match
          case "mirror" => Hand.Mirror
          case "flame" => Hand.Flame
          case "loom" => Hand.Loom
          case "depth" => Hand.Depth
          case "song" => Hand.Song
          case "silence" => Hand.Silence
          case _ => throw new IllegalArgumentException(s"Unknown hand: $handName")
        
        val synthesizers = system.getSynthesizersByHand(hand)
        synthesizers.map(CivicAngelAPI.AgentInfo.fromAgent)
      }.toEither.left.map(_.getMessage)
    }
  
  /**
   * Recall memories by trigger
   */
  val recallMemories: ServerEndpoint[Any, Identity] =
    CivicAngelAPI.recallMemoriesEndpoint.serverLogicSuccess { trigger =>
      system.recallMemories(trigger)
    }
  
  /**
   * Advance the lifecycle phase
   */
  val advanceLifecycle: ServerEndpoint[Any, Identity] =
    CivicAngelAPI.advanceLifecycleEndpoint.serverLogicSuccess { _ =>
      val newPhase = system.advanceLifecycle()
      CivicAngelAPI.LifecycleInfo.fromPhase(newPhase)
    }
  
  /**
   * Get current lifecycle phase
   */
  val getLifecycle: ServerEndpoint[Any, Identity] =
    CivicAngelAPI.getLifecycleEndpoint.serverLogicSuccess { _ =>
      CivicAngelAPI.LifecycleInfo.fromPhase(system.getCurrentPhase)
    }
  
  /**
   * Invoke a gesture
   */
  val gesture: ServerEndpoint[Any, Identity] =
    CivicAngelAPI.gestureEndpoint.serverLogicSuccess { invocation =>
      val input = SymbolicInput(
        description = invocation.description,
        gesture = Some(invocation.gesture),
        symbol = invocation.symbol
      )
      system.activate(input)
    }
  
  /**
   * All endpoints
   */
  def all: List[ServerEndpoint[Any, Identity]] = List(
    info,
    activate,
    listAgents,
    getAgent,
    getHand,
    recallMemories,
    advanceLifecycle,
    getLifecycle,
    gesture
  )
