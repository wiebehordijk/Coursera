package recfun

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
  }

  /**
    * Exercise 1
    */
  def pascal(c: Int, r: Int): Int = {
    assert(c >= 0)
    assert(r >= 0)
    assert(c <= r)

    if (c == 0 || c == r) 1
    else pascal(c - 1, r - 1) + pascal(c, r - 1)
  }


  /**
    * Exercise 2
    */
  def balance(chars: List[Char]) = {

    def balance(chars: List[Char], depth: Int): Boolean = chars match {
      case List() => depth == 0
      case '(' :: rest => balance(rest, depth + 1)
      case ')' :: rest => if (depth == 0) false else balance(rest, depth - 1)
      case _ :: rest => balance(rest, depth)
    }

    balance(chars, 0)
  }


  /**
    * Exercise 3
    */
  def countChange(money: Int, coins: List[Int]): Int = {
    if (money == 0) 1
    else if (money < 0) 0
    else if (coins.isEmpty) 0
    else countChange(money - coins.head, coins) + countChange(money, coins.tail)
  }

}
