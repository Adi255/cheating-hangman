package hangman

import akka.actor.ActorSystem
import akka.testkit.TestProbe
import hangman.GameMaster.RegisterPlayer
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class GameMasterSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  implicit val system = ActorSystem()

  override def afterAll(): Unit = system.terminate()

  behavior of "Game Master"

  it should "register new player" in {

    val probe = TestProbe()

    val masterActor = system.actorOf(GameMaster.props())
    masterActor.tell(RegisterPlayer("p1", 1L), probe.ref)
    probe.expectMsg(Player.PlayerRegistered)
    val player1 = probe.lastSender

    masterActor.tell(RegisterPlayer("p2", 2L), probe.ref)
    probe.expectMsg(Player.PlayerRegistered)
    val player2 = probe.lastSender

    player1 should !==(player2)
//    player1.tell(TurnPrompt, probe.ref)
//    probe.expectMsg(Player.LetterGuess('x'))
//    player2.tell(TurnPrompt, probe.ref)
//    probe.expectMsg(Player.LetterGuess('x'))
  }

}
