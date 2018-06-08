package hangman

import akka.actor.ActorSystem
import akka.testkit.TestProbe
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class PlayerSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  implicit val system = ActorSystem()

  override def afterAll(): Unit = system.terminate()

  behavior of "Player"

  it should "respond with a letter guess after a prompt" in {
    val probe = TestProbe()
    val playerActor = system.actorOf(Player.props("p1", 1L))
    playerActor.tell(Player.TurnPrompt, probe.ref)
    probe.expectMsg(Player.GuessRequest)
  }

  it should "accept a letter guess and submit turn" in {
    val probe = TestProbe()
    val playerActor = system.actorOf(Player.props("p2", 2L))
    playerActor.tell(Player.LetterGuess('x', 2l), probe.ref)
    probe.expectMsg(Player.TakeTurn)
  }
}