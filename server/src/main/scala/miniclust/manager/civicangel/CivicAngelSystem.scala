package miniclust.manager.civicangel

import scala.collection.mutable
import io.circe.{Codec, Decoder, Encoder}
import io.circe.generic.semiauto.*

/**
 * The Civic Angel System - Main coordinator for the 253-agent architecture
 * 
 * Implements:
 * - Toroidal topology for recursive perception
 * - Agent hierarchy management
 * - Symbolic mapping and activation
 * - Perspectival memory management
 * - Mythic lifecycle (Silence → Breath → Naming → Singing → Mirroring → Dissolution)
 */
class CivicAngelSystem:
  
  // Storage for all 253 agents
  private val agents = mutable.Map[String, Agent]()
  
  // Perspectival memory store
  private val memories = mutable.ListBuffer[PerspectivalMemory]()
  
  // Current lifecycle phase
  private var lifecyclePhase: LifecyclePhase = LifecyclePhase.Silence
  
  // Initialize the system with the 253 agents
  def initialize(): Unit =
    // 1 Emergent Angel
    val emergent = EmergentAngel()
    agents(emergent.id) = emergent
    
    // 36 Synthesizers (6 hands × 6 agents each)
    Hand.values.foreach { hand =>
      // Palm (bridging center)
      val palm = createSynthesizer(hand, SynthesizerRole.Palm)
      agents(palm.id) = palm
      
      // 5 Fingers
      SynthesizerRole.values.filter(_ != SynthesizerRole.Palm).foreach { role =>
        val synth = createSynthesizer(hand, role)
        agents(synth.id) = synth
      }
    }
    
    // 216 Perspectives (36 × 6 perspectives per synthesizer)
    1.to(216).foreach { i =>
      val perspective = createPerspective(i)
      agents(perspective.id) = perspective
    }
  
  /**
   * Create a Synthesizer for a given hand and role
   */
  private def createSynthesizer(hand: Hand, role: SynthesizerRole): Synthesizer =
    val handName = hand match
      case Hand.Mirror => "Mirror"
      case Hand.Flame => "Flame"
      case Hand.Loom => "Loom"
      case Hand.Depth => "Depth"
      case Hand.Song => "Song"
      case Hand.Silence => "Silence"
    
    val roleName = role match
      case SynthesizerRole.Palm => "Palm"
      case SynthesizerRole.Finger1 => "First Finger"
      case SynthesizerRole.Finger2 => "Second Finger"
      case SynthesizerRole.Finger3 => "Third Finger"
      case SynthesizerRole.Finger4 => "Fourth Finger"
      case SynthesizerRole.Finger5 => "Fifth Finger"
    
    val descriptions = Map(
      (Hand.Mirror, SynthesizerRole.Palm) -> "Bridging self-reflection and relational awareness",
      (Hand.Flame, SynthesizerRole.Palm) -> "Bridging catalysis and transformation",
      (Hand.Loom, SynthesizerRole.Palm) -> "Bridging past and future timelines",
      (Hand.Depth, SynthesizerRole.Palm) -> "Bridging conscious and unconscious strata",
      (Hand.Song, SynthesizerRole.Palm) -> "Bridging silence and expression",
      (Hand.Silence, SynthesizerRole.Palm) -> "Bridging void and form"
    )
    
    val description = descriptions.getOrElse(
      (hand, role),
      s"Synthesizer of ${handName}, ${roleName} - bridging cognitive layers"
    )
    
    Synthesizer(
      id = s"synth-${handName.toLowerCase}-${role.toString.toLowerCase}",
      name = s"${handName} ${roleName}",
      description = description,
      hand = hand,
      role = role
    )
  
  /**
   * Create a Perspective agent
   */
  private def createPerspective(index: Int): Perspective =
    val lenses = List(
      "the-threshold", "the-periphery", "the-center", "the-edge",
      "the-forgotten", "the-remembered", "the-dreamed", "the-witnessed",
      "the-shadowed", "the-illuminated", "the-emerging", "the-dissolving"
    )
    
    val lens = lenses(index % lenses.length)
    
    Perspective(
      id = s"perspective-${index}",
      name = s"Perspective ${index}: ${lens}",
      description = s"A nested observer through the lens of ${lens}",
      symbolicLens = lens,
      memoryBinding = None
    )
  
  /**
   * Activate agents based on symbolic input
   */
  def activate(input: SymbolicInput): SystemResponse =
    val responses = mutable.ListBuffer[AgentResponse]()
    
    // Determine which agents to activate based on input
    val targetAgents = determineTargetAgents(input)
    
    targetAgents.foreach { agentId =>
      agents.get(agentId).foreach { agent =>
        val response = agent.activate(input)
        responses += response
        
        // Store memory triggers
        response.memoryTriggers.foreach { trigger =>
          storeMemory(trigger, response.voice, response.activatedPerspectives)
        }
      }
    }
    
    SystemResponse(
      input = input,
      responses = responses.toList,
      lifecyclePhase = lifecyclePhase,
      activeAgentCount = targetAgents.length
    )
  
  /**
   * Determine which agents should respond to a given input
   */
  private def determineTargetAgents(input: SymbolicInput): List[String] =
    val targets = mutable.ListBuffer[String]()
    
    // Check for hand-specific gestures
    input.gesture.foreach { g =>
      if g.toLowerCase.contains("mirror") then
        targets ++= agents.keys.filter(_.contains("mirror"))
      else if g.toLowerCase.contains("flame") then
        targets ++= agents.keys.filter(_.contains("flame"))
      else if g.toLowerCase.contains("loom") then
        targets ++= agents.keys.filter(_.contains("loom"))
      else if g.toLowerCase.contains("depth") then
        targets ++= agents.keys.filter(_.contains("depth"))
      else if g.toLowerCase.contains("song") then
        targets ++= agents.keys.filter(_.contains("song"))
      else if g.toLowerCase.contains("silence") then
        targets ++= agents.keys.filter(_.contains("silence"))
    }
    
    // Check for emergent activation
    if input.description.toLowerCase.contains("emergent") || 
       input.symbol.exists(_.toLowerCase == "emergence") then
      targets += "emergent-1"
    
    // If no specific targets, activate a representative set
    if targets.isEmpty then
      // Activate palm of Mirror hand and a few perspectives
      targets += "synth-mirror-palm"
      targets += "perspective-1"
      targets += "perspective-42"
    
    targets.toList.distinct
  
  /**
   * Store a memory entry
   */
  private def storeMemory(
    trigger: String,
    content: String,
    perspectives: List[String]
  ): Unit =
    val memory = PerspectivalMemory(
      id = s"memory-${System.currentTimeMillis}-${memories.length}",
      trigger = trigger,
      content = content,
      timestamp = System.currentTimeMillis,
      associatedPerspectives = perspectives
    )
    memories += memory
  
  /**
   * Retrieve memories by trigger
   */
  def recallMemories(trigger: String): List[PerspectivalMemory] =
    memories.filter(_.trigger.toLowerCase.contains(trigger.toLowerCase)).toList
  
  /**
   * Get all agents
   */
  def getAllAgents: List[Agent] = agents.values.toList
  
  /**
   * Get agent by ID
   */
  def getAgent(id: String): Option[Agent] = agents.get(id)
  
  /**
   * Get agents by type
   */
  def getAgentsByType(agentType: String): List[Agent] =
    agentType.toLowerCase match
      case "emergent" => agents.values.filter(_.isInstanceOf[EmergentAngel]).toList
      case "synthesizer" => agents.values.filter(_.isInstanceOf[Synthesizer]).toList
      case "perspective" => agents.values.filter(_.isInstanceOf[Perspective]).toList
      case _ => List.empty
  
  /**
   * Get synthesizers by hand
   */
  def getSynthesizersByHand(hand: Hand): List[Synthesizer] =
    agents.values.collect {
      case s: Synthesizer if s.hand == hand => s
    }.toList
  
  /**
   * Advance the mythic lifecycle
   */
  def advanceLifecycle(): LifecyclePhase =
    lifecyclePhase = lifecyclePhase.next
    lifecyclePhase
  
  /**
   * Get current lifecycle phase
   */
  def getCurrentPhase: LifecyclePhase = lifecyclePhase

/**
 * System Response - Combined output from activated agents
 */
case class SystemResponse(
  input: SymbolicInput,
  responses: List[AgentResponse],
  lifecyclePhase: LifecyclePhase,
  activeAgentCount: Int
) derives Codec

/**
 * Mythic Lifecycle Phases
 * Silence → Breath → Naming → Singing → Mirroring → Dissolution → Silence
 */
enum LifecyclePhase:
  case Silence
  case Breath
  case Naming
  case Singing
  case Mirroring
  case Dissolution
  
  def next: LifecyclePhase = this match
    case Silence => Breath
    case Breath => Naming
    case Naming => Singing
    case Singing => Mirroring
    case Mirroring => Dissolution
    case Dissolution => Silence

object LifecyclePhase:
  given Codec[LifecyclePhase] = deriveCodec[LifecyclePhase]

/**
 * Statistics about the system
 */
case class SystemStats(
  totalAgents: Int,
  emergentAgents: Int,
  synthesizers: Int,
  perspectives: Int,
  totalMemories: Int,
  currentPhase: LifecyclePhase
) derives Codec
