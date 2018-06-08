package hangman.logic

import org.scalatest.{FlatSpec, Matchers}

class GameLogicSpec extends FlatSpec with Matchers {

  /*
    * - Let's imagine we have a small dictionary with the words "cat", "car", "cow", "dog", "pig"
    * - The computer picks the word "cat" and tells you the number of letters you have to guess, which in this case is three: "- - -"
    * - You guess "o", which matches no letters, so the computer sticks with "cat" and displays "- - -"
    * - You guess "i", which matches no letters, so the computer sticks with "cat" and displays "- - -"
    * - You guess "c", which matches the first letter of "cat". The computer tries to switch words but it can't because "cow", "dog", and "pig" contain letters that it said were not in the word.
    * It could switch to "car" but doing so would not help, so it sticks with "cat". It concedes letter "c"  and displays "c - -".
    * - You guess "a", which matches the second letter of "cat". The computer tries to switch words but it can't because both "cat" and "car" match that letter, so it concedes "a" and displays "c a -"
    * - You guess "t", which matches the third letter of "cat". The computer can change its word to "car" to avoid conceding a letter so it does so, and tells you it didn't match and displays "c a -"
    * - You guess "r", which matches the third letter of "car". The computer has no other words it can switch to so it concedes the game.
    */

  behavior of "GameLogic"

  it should "update correctly when guess is wrong" in {

    val logic = GameLogic("---", Set(), "cat", Set("dog", "pig"), false)

    val newLogic = logic.guess('d')

    newLogic shouldBe GameLogic("---", Set('d'), "cat", Set("pig"), false)
  }

  it should "change answers if it can" in {

    val logic = GameLogic("ca-e", Set('c', 'a', 'x'), "cape", Set("cage"), false)

    val newLogic = logic.guess('p')

    newLogic shouldBe GameLogic("ca-e", Set('a', 'c', 'p', 'x'), "cage", Set(), false)
  }

  it should "concede correct placement if no alternative" in {
    val logic = GameLogic("---", Set('o', 'i'), "cat", Set("car"), false)

    val newLogic = logic.guess('c')

    newLogic shouldBe GameLogic("c--", Set('o', 'i', 'c'), "cat", Set("car"), true)
  }

  it should "indicate a correct answer" in {
    val logic = GameLogic("ca-", Set('o', 'i', 'c'), "car", Set(), false)

    val newLogic = logic.guess('r')

    newLogic shouldBe GameLogic("car", Set('o', 'i', 'c', 'r'), "car", Set(), true)
  }

  it should "not switch answer if it doesn't help" in {
    val logic = GameLogic("c--", Set('o', 'i', 'c'), "cat", Set("car"), false)

    val newLogic = logic.guess('a')

    newLogic shouldBe GameLogic("ca-", Set('o', 'i', 'c', 'a'), "cat", Set("car"), true)
  }

}
