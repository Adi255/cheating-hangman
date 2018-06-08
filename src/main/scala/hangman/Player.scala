package hangman

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import hangman.GameMaster.RegisterPlayer
import hangman.Player._

object Player {
  def props(name: String, id: Long): Props = Props(new Player(name, id))

  case object TurnPrompt

  case object GuessRequest

  case class LetterGuess(letter: Char, playerId: Long)

  case object PlayerRegistered

  case object TakeTurn

  case object LoseLife

}


class Player(name: String, id: Long) extends Actor with ActorLogging {

  private var lives: Int = 10

  override def preStart(): Unit = log.info("Player {} added to game", name)

  override def postStop(): Unit = log.info("Player actor {} stopped", name)

  override def receive: Receive = {
    case LetterGuess(letter, _) =>
      log.info("Player {} guesses '{}'", name, letter)
      sender() ! TakeTurn
    case TurnPrompt =>
      log.info("Player {} to guess next", name)
      sender() ! GuessRequest
    case RegisterPlayer(_, _) => sender() ! Player.PlayerRegistered
    case LoseLife =>
      lives = lives - 1
      log.info(s"Player $name has $lives lives remaining")
      HangmanStates.printState(lives)
      if (lives == 0) log.info("You lose!")
  }
}
