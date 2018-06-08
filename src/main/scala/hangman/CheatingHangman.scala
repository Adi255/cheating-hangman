package hangman

import akka.actor.{Actor, ActorLogging, Props}
import hangman.logic.GameLogic

import scala.util.Random

object CheatingHangman {
  def props(wordSet: Set[String]): Props = Props(new CheatingHangman(wordSet, debug = true))

  case object Winner

  case class NewPhrase(guessPhrase: String, success: Boolean)

}

class CheatingHangman(wordSet: Set[String], debug: Boolean) extends Actor with ActorLogging {

  private def initGameLogic(): GameLogic = {
    val answer = wordSet.toList(Random.nextInt(wordSet.size))
    if(debug) log.info("Starting with answer {}", answer)
    GameLogic(answer.map(_ => "-").mkString, Set(), answer, wordSet - answer, false)
  }

  private var gameLogic = initGameLogic()

  override def receive: Receive = {
    case Player.LetterGuess(char, _) =>
      gameLogic = gameLogic.guess(char)
      if (debug)
        log.info("current answer: {}, {} alternatives", gameLogic.answer, gameLogic.alternatives.size)
      if (gameLogic.isComplete)
        sender() ! CheatingHangman.Winner
      else
        sender() ! CheatingHangman.NewPhrase(gameLogic.phraseSoFar,gameLogic.conceded)
  }
}
