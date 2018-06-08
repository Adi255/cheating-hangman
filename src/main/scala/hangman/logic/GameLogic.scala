package hangman.logic

case class GameLogic(phraseSoFar: String, currentGuesses: Set[Char], answer: String, alternatives: Set[String], conceded: Boolean) {

  def guess(guess: Char): GameLogic = {
    val potential = updateCurrentGuess(guess, answer)
    if (potential != phraseSoFar) {
      val newLogic = tryCheating(guess, potential)
      newLogic
    }
    else
      GameLogic(phraseSoFar, currentGuesses + guess, answer, eliminateAlternatives(guess), false)
  }

  private def tryCheating(guess: Char, potential: String) = {
    //    val newAlternatives = findNewAlternatives(guess, potential)
    val dontContainChar = alternatives.filter(word => !word.contains(guess))
    if (dontContainChar.nonEmpty) {
      GameLogic(phraseSoFar, currentGuesses + guess, dontContainChar.head, dontContainChar.tail, false)
      //don't concede
    } else {
      //concede
      val positions = characterPostions(guess)
      val newAlternatives = alternatives.filter(word => containsCharAtPositions(word, guess, positions))
//      if (newAlternatives.nonEmpty)
      GameLogic(potential, currentGuesses + guess, answer, newAlternatives, true)
//      else
//        GameLogic(potential, currentGuesses + guess, answer, newAlternatives)
    }
  }

  private def updateCurrentGuess(guess: Char, target: String) =
    phraseSoFar.zip(answer).map {
      case (a, b) => if (a != b && guess == b) guess else a
    }.mkString

  private def findNewAlternatives(newGuess: Char, potentialPhrase: String): Set[String] = {
    val dontContainChar = alternatives.filter(word => !word.contains(newGuess))
    if (dontContainChar.nonEmpty) {
      dontContainChar
      //don't concede
    } else {
      //concede
      val positions = characterPostions(newGuess)
      alternatives.filter(word => containsCharAtPositions(word, newGuess, positions))
    }
  }

  private def characterPostions(guess: Char) =
    answer.zipWithIndex.filter { case (char, pos) => char == guess }.map(_._2)

  private def containsCharAtPositions(word: String, char: Char, positions: Seq[Int]) = {
    val charsAtPosition = (for (pos <- positions) yield word(pos)).toSet
    charsAtPosition.forall(_ == char)
  }

  private def eliminateAlternatives(guess: Char): Set[String] = alternatives.filter(!_.contains(guess))

  def isComplete: Boolean = phraseSoFar == answer
}
