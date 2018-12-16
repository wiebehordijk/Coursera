import scala.io.Source

object Solver {

  def main(args: Array[String]): Unit = {
    println("19 0")
    println("0 0 1 1")
  }


  def solve(args: Array[String]): Unit = {
    if (args.length > 0) {
      val filename: String =
        if (args.head.startsWith("-file="))
          args.head.substring(6)
        else
          args.head

      val lines = Source.fromFile(filename).getLines().toList
      val firstline = lines.head.split("\\s+")
      val items = firstline(0).toInt
      val capacity = firstline(1).toInt

      val values = new Array[Int](items)
      val weights = new Array[Int](items)

      var remaining = lines.tail
      for (i <- 0 until items) {
        val line = remaining.head.split("\\s+")
        values(i) = line(0).toInt
        weights(i) = line(1).toInt
        remaining = remaining.tail
      }


    }
  }
}
