import scala.io.Source

object Solver {

  private var numItems: Int = 0
  private var capacity: Int = 0
  private var items: List[Item] = _


  def main(args: Array[String]): Unit = {
    if (args.length > 0) {
      val filename: String =
        if (args.head.startsWith("-file="))
          args.head.substring(6)
        else
          args.head

      read(filename)

      solveDynamic()

      println(items.filter(_.taken).map(_.value).sum + " 1")
      for (item <- items) {
        if (item.taken)
          print("1 ")
        else
          print("0 ")
      }
      println
    }
  }

  def solveDynamic(): Unit = {
    val t = Array.ofDim[Int](numItems + 1, capacity + 1)
    for {
      (item, i) <- items.zip(Stream from 1)
      c <- 0 to capacity
    } {
      t(i)(c) = {
        if (c >= item.weight)
          Math.max(t(i-1)(c - item.weight) + item.value, t(i-1)(c))
        else
          t(i-1)(c)
      }
    }

    var c = capacity
    for {
      (item, i) <- items.zip(Stream from 1).reverse
    } {
      if (t(i)(c) > t(i-1)(c)) {
        c -= item.weight
        item.taken = true
      }
    }
  }

  def solveBinary(): Unit = {
    var bestSolutionValue = 0
    var bestSolution: List[Item] = null

    def solveBinaryDfs(itemsRemaining: List[Item], remainingCapacity: Int, selected: List[Item], value: Int): Unit = {

      def optimisticGuess(): Double = {
        var guess: Double = value
        var room = remainingCapacity
        var it = itemsRemaining
        while (it.nonEmpty && room - it.head.weight > 0) {
          guess += it.head.value
          room -= it.head.weight
          it = it.tail
        }
        if (it.nonEmpty)
          guess += (room.toDouble * it.head.value / it.head.weight.toDouble).ceil
        guess
      }

      if (itemsRemaining.isEmpty) {
        if (remainingCapacity >= 0 && value > bestSolutionValue) {
          bestSolutionValue = value
          bestSolution = selected
        }
      }
      else if (remainingCapacity > 0 && optimisticGuess() > bestSolutionValue) {
        solveBinaryDfs(itemsRemaining.tail, remainingCapacity - itemsRemaining.head.weight,
          itemsRemaining.head :: selected, value + itemsRemaining.head.value)
        solveBinaryDfs(itemsRemaining.tail, remainingCapacity, selected, value)
      }
    }

    solveBinaryDfs(items.sortBy(i => -1.0 * i.value / i.weight), capacity, List[Item](), 0)

    if (bestSolutionValue > 0)
      for (item <- bestSolution)
        item.taken = true
  }


  def solveGreedy(): Unit = {
    val sortedItems = items.sortBy(i => 1.0 * i.value / i.weight)
    var weight = 0

    for (item <- sortedItems) {
      if (weight + item.weight <= capacity) {
        item.taken = true
        weight += item.weight
      }
    }
  }

  def read(filename: String): Unit = {
    val lines = Source.fromFile(filename).getLines()
    val firstline = lines.next().split("\\s+")
    numItems = firstline(0).toInt
    capacity = firstline(1).toInt
    items = lines map toItem toList
  }

  def toItem(s: String): Item = {
    val parts = s.split("\\s+")
    val v = parts(0).toInt
    val w = parts(1).toInt
    Item(v, w)
  }

}


case class Item(value: Int, weight: Int) {
  var taken: Boolean = false
}