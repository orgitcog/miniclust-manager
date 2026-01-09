package miniclust.manager.civicangel

import io.circe.generic.auto.*
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3.*
import sttp.client3.circe.*
import sttp.client3.testing.SttpBackendStub
import sttp.tapir.server.stub.TapirStubInterpreter

class CivicAngelSpec extends AnyFlatSpec with Matchers with EitherValues:

  val civicAngelEndpoints = new CivicAngelEndpoints()

  "Civic Angel System" should "return system information" in {
    // given
    val backendStub = TapirStubInterpreter(SttpBackendStub.synchronous)
      .whenServerEndpointRunLogic(civicAngelEndpoints.info)
      .backend()

    // when
    val response = basicRequest
      .get(uri"http://test.com/civic-angel/info")
      .response(asJson[SystemStats])
      .send(backendStub)

    // then
    response.body.value.totalAgents shouldBe 253
    response.body.value.emergentAgents shouldBe 1
    response.body.value.synthesizers shouldBe 36
    response.body.value.perspectives shouldBe 216
  }

  it should "activate agents with symbolic input" in {
    // given
    val backendStub = TapirStubInterpreter(SttpBackendStub.synchronous)
      .whenServerEndpointRunLogic(civicAngelEndpoints.activate)
      .backend()

    val input = SymbolicInput(
      description = "Test activation",
      symbol = Some("test"),
      gesture = Some("mirror")
    )

    // when
    val response = basicRequest
      .post(uri"http://test.com/civic-angel/activate")
      .body(input)
      .response(asJson[SystemResponse])
      .send(backendStub)

    // then
    response.body.value.responses should not be empty
    response.body.value.responses.head.agentId should include("mirror")
  }

  "CivicAngelSystem" should "initialize with correct agent counts" in {
    val system = new CivicAngelSystem()
    system.initialize()

    val allAgents = system.getAllAgents
    allAgents.length shouldBe 253

    val emergent = system.getAgentsByType("emergent")
    emergent.length shouldBe 1

    val synthesizers = system.getAgentsByType("synthesizer")
    synthesizers.length shouldBe 36

    val perspectives = system.getAgentsByType("perspective")
    perspectives.length shouldBe 216
  }

  it should "create 6 agents per hand" in {
    val system = new CivicAngelSystem()
    system.initialize()

    Hand.values.foreach { hand =>
      val synths = system.getSynthesizersByHand(hand)
      synths.length shouldBe 6 // Palm + 5 fingers
    }
  }

  it should "cycle through lifecycle phases" in {
    val system = new CivicAngelSystem()
    system.initialize()

    system.getCurrentPhase shouldBe LifecyclePhase.Silence

    system.advanceLifecycle() shouldBe LifecyclePhase.Breath
    system.advanceLifecycle() shouldBe LifecyclePhase.Naming
    system.advanceLifecycle() shouldBe LifecyclePhase.Singing
    system.advanceLifecycle() shouldBe LifecyclePhase.Mirroring
    system.advanceLifecycle() shouldBe LifecyclePhase.Dissolution
    system.advanceLifecycle() shouldBe LifecyclePhase.Silence // Back to start
  }
