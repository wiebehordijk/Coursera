package streams

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import Bloxorz._

@RunWith(classOf[JUnitRunner])
class BloxorzSuite extends FunSuite {

  trait SolutionChecker extends GameDef with Solver with StringParserTerrain {
    /**
      * This method applies a list of moves `ls` to the block at position
      * `startPos`. This can be used to verify if a certain list of moves
      * is a valid solution, i.e. leads to the goal.
      */
    def solve(ls: List[Move]): Block =
    ls.foldLeft(startBlock) ((block, move) => move(block))
  }

  trait Level1 extends SolutionChecker {
    /* terrain for level 1*/

    val level =
      """ooo-------
        |oSoooo----
        |ooooooooo-
        |-ooooooooo
        |-----ooToo
        |------ooo-""".stripMargin

    val optsolution = List(Right, Right, Down, Right, Right, Right, Down)

    val block1 = Block(Pos(1, 1), Pos(1, 1))
    val block2 = Block(Pos(1, 1), Pos(1, 2))
    val block3 = Block(Pos(1, 1), Pos(2, 1))
    val block4 = Block(Pos(0, 1), Pos(0, 2))
    val block5 = Block(Pos(0, 2), Pos(0, 3))
    val block6 = Block(Pos(0, 3), Pos(0, 4))
    val block7 = Block(Pos(3, 0), Pos(3, 0))
    val block8 = Block(Pos(4, 7), Pos(4, 7))
  }


  test("terrain function level 1") {
    new Level1 {
      assert(terrain(Pos(0, 0)), "0,0")
      assert(terrain(Pos(1, 1)), "1,1") // start
      assert(terrain(Pos(4, 7)), "4,7") // goal
      assert(terrain(Pos(5, 8)), "5,8")
      assert(!terrain(Pos(5, 9)), "5,9")
      assert(terrain(Pos(4, 9)), "4,9")
      assert(!terrain(Pos(6, 8)), "6,8")
      assert(!terrain(Pos(4, 11)), "4,11")
      assert(!terrain(Pos(-1, 0)), "-1,0")
      assert(!terrain(Pos(0, -1)), "0,-1")
    }
  }

  test("findChar level 1") {
    new Level1 {
      assert(startPos == Pos(1, 1))
    }
  }

  test("isStanding") {
    new Level1 {
      assert(block1.isStanding, "(1,1), (1,1)")
      assert(!block2.isStanding, "(1,1), (1,2)")
      assert(!block3.isStanding, "(1,1), (2,1)")
    }
  }

  test("isLegal") {
    new Level1 {
      assert(block1.isLegal)
      assert(block2.isLegal)
      assert(block3.isLegal)
      assert(block4.isLegal)
      assert(!block5.isLegal)
      assert(!block6.isLegal)
      assert(!block7.isLegal)
    }
  }

  test("startBlock") {
    new Level1 {
      assert(startBlock == block1)
    }
  }

  test("neighbors") {
    new Level1 {
      val neighbors = block1.neighbors
      assert(neighbors.length == 4, "should have 4 neighbors")
      assert(neighbors.contains(Block(Pos(1, -1), Pos(1, 0)), Left))
      assert(neighbors.contains(Block(Pos(1, 2), Pos(1, 3)), Right))
      assert(neighbors.contains(Block(Pos(-1, 1), Pos(0, 1)), Up))
      assert(neighbors.contains(Block(Pos(2, 1), Pos(3, 1)), Down))
    }
  }

  test("legalNeighbors") {
    new Level1 {
      val neighbors = block1.legalNeighbors
      assert(neighbors.length == 2, "should have 2 legal neighbors")
      assert(neighbors.contains(Block(Pos(1, 2), Pos(1, 3)), Right))
      assert(neighbors.contains(Block(Pos(2, 1), Pos(3, 1)), Down))
    }
  }

  test("done") {
    new Level1 {
      assert(done(block8))
      assert(!done(block2))
    }
  }

  test("neighborsWithHistory") {
    new Level1 {
      val nwh = neighborsWithHistory(block1, List(Left, Up))
      val nwhs = nwh.toSet
      assert(nwhs.size == 2, "should have 2 legal neighbors")
      assert(nwhs.contains(Block(Pos(1, 2), Pos(1, 3)), List(Right, Left, Up)))
      assert(nwhs.contains(Block(Pos(2, 1), Pos(3, 1)), List(Down, Left, Up)))
    }
  }

  test("newNeighborsOnly") {
    new Level1 {
      val nno = newNeighborsOnly(
        Set(
          (Block(Pos(1, 2), Pos(1, 3)), List(Right, Left, Up)),
          (Block(Pos(2, 1), Pos(3, 1)), List(Down, Left, Up))
        ).toStream,

        Set(Block(Pos(1, 2), Pos(1, 3)), Block(Pos(1, 1), Pos(1, 1)))
      )

      val nnos = nno.toSet
      assert(nnos.size == 1, "only one new neighbor")
      assert(nnos.contains((Block(Pos(2, 1), Pos(3, 1)), List(Down, Left, Up))))
    }
  }

  test("from") {
    new Level1 {
      val fr = from(
        Set(Path(List(Up, Left), Block(Pos(1, 1), Pos(1, 1)))).toStream,
        Set(Block(Pos(1, 2), Pos(1, 3)))
      ).toList

      //assert(fr.length == 2, "should contain initial plus one neighbor")
      assert(fr.head == Path(List(Up, Left), Block(Pos(1, 1), Pos(1, 1))), "should start with initial")
      assert(fr(1) == Path(List(Down, Up, Left), Block(Pos(2, 1), Pos(3, 1))), "legal neighbor not explored")
      assert(fr(2) == Path(List(Right, Down, Up, Left), Block(Pos(2, 2), Pos(3, 2))), "legal neighbor not explored")
    }
  }

  test("optimal solution for level 1") {
    new Level1 {
      assert(solve(solution) == Block(goal, goal))
    }
  }


  test("optimal solution length for level 1") {
    new Level1 {
      assert(solution.length == optsolution.length)
    }
  }
}
