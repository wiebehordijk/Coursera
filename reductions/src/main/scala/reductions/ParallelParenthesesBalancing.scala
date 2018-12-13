package reductions

import scala.annotation._
import org.scalameter._
import common._

object ParallelParenthesesBalancingRunner {

  @volatile var seqResult = false

  @volatile var parResult = false

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 40,
    Key.exec.maxWarmupRuns -> 80,
    Key.exec.benchRuns -> 120,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val length = 100000000
    val chars = new Array[Char](length)
    val threshold = 10000
    val seqtime = standardConfig measure {
      seqResult = ParallelParenthesesBalancing.balance(chars)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential balancing time: $seqtime ms")

    val fjtime = standardConfig measure {
      parResult = ParallelParenthesesBalancing.parBalance(chars, threshold)
    }
    println(s"parallel result = $parResult")
    println(s"parallel balancing time: $fjtime ms")
    println(s"speedup: ${seqtime / fjtime}")
  }
}

object ParallelParenthesesBalancing {

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def balance(chars: Array[Char]): Boolean = {

    def balance(chars: Array[Char], from: Int, until: Int, depth: Int): Boolean = {
      if (from == until) depth == 0
      else if (chars(from) == '(') balance(chars, from + 1, until, depth + 1)
      else if (chars(from) == ')') balance(chars, from + 1, until, depth - 1)
      else balance(chars, from + 1, until, depth)
    }

    balance(chars, 0, chars.length, 0)
  }

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def parBalance(chars: Array[Char], threshold: Int): Boolean = {

    /** Returns pair of (endDepth, minDepth) of the segment */
    def traverse(idx: Int, until: Int): (Int, Int) = {
      var depth, minDepth = 0
      var i = idx
      while (i < until) {
        if (chars(i) == '(') depth += 1
        else if (chars(i) == ')') depth -= 1
        if (depth < minDepth) minDepth = depth
        i += 1
      }
      (depth, minDepth)
    }

    /** The end depth of two consecutive sections is the sum of their respective end depths.
      * The minimum depth of two consecutive sections is either the minimum depth of the left part, or
      * the minimum depth of the right part plus the end depth of the left part, whichever is lower.
      * @return pair (endDepth, minDepth)
      */
    def reduce(from: Int, until: Int): (Int, Int) = {
      if (until - from <= threshold) traverse(from, until)
      else {
        val mid = (from + until) / 2
        val ((depthLeft, minDepthLeft), (depthRight, minDepthRight)) =
          parallel(reduce(from, mid), reduce(mid, until))
        val endDepth = depthLeft + depthRight
        val minDepth = minDepthLeft min (depthLeft + minDepthRight)
        (endDepth, minDepth)
      }
    }

    // If the minimum depth anywhere in the string is below 0 the string is unbalanced
    // (minDepth cannot be above 0). The total depth at the end of the string must also be 0.
    reduce(0, chars.length) == (0, 0)
  }

  // For those who want more:
  // Prove that your reduction operator is associative!

}
