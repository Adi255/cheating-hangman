package hangman

import akka.actor.ActorSystem

import scala.io.StdIn

object HangmanApp extends App {
  val system = ActorSystem()

  try {
    val gm = system.actorOf(GameMaster.props())
    gm ! GameMaster.RegisterPlayer("me", 1l)
    gm ! GameMaster.StartGame(5)

    while(true){
      println("guess: ")
      val guess = StdIn.readChar()
      gm ! Player.LetterGuess(guess, 1)
    }
    StdIn.readLine()
  } finally {
    system.terminate()
  }
}
