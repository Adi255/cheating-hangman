package hangman

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import hangman.GameMaster.RegisterPlayer
import hangman.Player.TurnPrompt
import akka.pattern.ask
import akka.util.Timeout
import hangman.CheatingHangman.NewPhrase

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.io.Source


object GameMaster {
  def props(): Props = Props(new GameMaster())

  case class RegisterPlayer(player: String, playerId: Long)

  case class StartGame(wordLength: Int)

}

class GameMaster extends Actor with ActorLogging {

  var playerIdToActor = Map.empty[Long, ActorRef]
  var actorToPlayerId = Map.empty[ActorRef, Long]
  var hangman: ActorRef = _

  private def createHangman(wordLength: Int): Unit = {
    val wordSet = Source.fromFile("src/main/resources/words.txt").getLines().filter(_.length == wordLength).toSet
    hangman = context.actorOf(CheatingHangman.props(wordSet))
  }

  override def receive: Receive = {
    case regPlayerMsg@RegisterPlayer(player, id) =>
      log.info("Creating player actor for player {}/{}", player, id)
      val playerActor = context.actorOf(Player.props(player, id), s"player-$id")
      context.watch(playerActor)
      actorToPlayerId += playerActor -> id
      playerIdToActor += id -> playerActor
      playerActor forward regPlayerMsg
    case GameMaster.StartGame(wordLength) =>
      createHangman(wordLength)
      log.info("Guess this one - {}", List.fill(wordLength)("-").mkString(" "))
      actorToPlayerId.keys.foreach(_ ! TurnPrompt)
    case guess@Player.LetterGuess(char, playerId) =>
      log.info("Guessing with {}", char)
      implicit val timeout = Timeout(5 seconds)
      val future = hangman ? guess
      val result = Await.result(future, timeout.duration)
      result match {
        case CheatingHangman.NewPhrase(phrase, succeeded) =>
          log.info("Current guess {}", phrase.mkString(" "))
          if(!succeeded) playerIdToActor(playerId) ! Player.LoseLife
          actorToPlayerId.keys.foreach(_ ! TurnPrompt)
        case CheatingHangman.Winner =>
          log.info("you win!")
      }

  }
}
