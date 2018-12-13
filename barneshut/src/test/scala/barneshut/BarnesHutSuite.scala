package barneshut

import java.util.concurrent._
import scala.collection._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common._
import scala.math._
import scala.collection.parallel._
import barneshut.conctrees.ConcBuffer

@RunWith(classOf[JUnitRunner])
class BarnesHutSuite extends FunSuite {

  // test cases for quad tree

import FloatOps._
  test("Empty: center of mass should be the center of the cell") {
    val quad = Empty(51f, 46.3f, 5f)
    assert(quad.massX == 51f, s"${quad.massX} should be 51f")
    assert(quad.massY == 46.3f, s"${quad.massY} should be 46.3f")
  }

  test("Empty: mass should be 0") {
    val quad = Empty(51f, 46.3f, 5f)
    assert(quad.mass == 0f, s"${quad.mass} should be 0f")
  }

  test("Empty: total should be 0") {
    val quad = Empty(51f, 46.3f, 5f)
    assert(quad.total == 0, s"${quad.total} should be 0")
  }

  test("Leaf with 1 body") {
    val b = new Body(123f, 18f, 26f, 0f, 0f)
    val quad = Leaf(17.5f, 27.5f, 5f, Seq(b))

    assert(quad.mass ~= 123f, s"${quad.mass} should be 123f")
    assert(quad.massX ~= 18f, s"${quad.massX} should be 18f")
    assert(quad.massY ~= 26f, s"${quad.massY} should be 26f")
    assert(quad.total == 1, s"${quad.total} should be 1")
  }

  test("Fork with 4 empty bodies") {
    val f = Fork(Empty(1,1,1),Empty(2,1,1),Empty(1,2,1),Empty(2,2,1))
    assert(f.centerY == 1.5f)
    assert(f.centerX == 1.5f)
    assert(f.mass == 0)
    assert(f.massX == 1.5f, "Let center of mass be at center of quad if no bodies")
    assert(f.massY == 1.5f, "Let center of mass be at center of quad if no bodies")
  }

  test("Fork with 3 empty quadrants and 1 leaf (nw)") {
    val b = new Body(123f, 18f, 26f, 0f, 0f)
    val nw = Leaf(17.5f, 27.5f, 5f, Seq(b))
    val ne = Empty(22.5f, 27.5f, 5f)
    val sw = Empty(17.5f, 32.5f, 5f)
    val se = Empty(22.5f, 32.5f, 5f)
    val quad = Fork(nw, ne, sw, se)

    assert(quad.centerX == 20f, s"${quad.centerX} should be 20f")
    assert(quad.centerY == 30f, s"${quad.centerY} should be 30f")
    assert(quad.mass ~= 123f, s"${quad.mass} should be 123f")
    assert(quad.massX ~= 18f, s"${quad.massX} should be 18f")
    assert(quad.massY ~= 26f, s"${quad.massY} should be 26f")
    assert(quad.total == 1, s"${quad.total} should be 1")
  }

  test("Empty.insert(b) should return a Leaf with only that body") {
    val quad = Empty(51f, 46.3f, 5f)
    val b = new Body(3f, 54f, 46f, 0f, 0f)
    val inserted = quad.insert(b)
    inserted match {
      case Leaf(centerX, centerY, size, bodies) =>
        assert(centerX == 51f, s"$centerX should be 51f")
        assert(centerY == 46.3f, s"$centerY should be 46.3f")
        assert(size == 5f, s"$size should be 5f")
        assert(bodies == Seq(b), s"$bodies should contain only the inserted body")
      case _ =>
        fail("Empty.insert() should have returned a Leaf, was $inserted")
    }
  }

  // test cases for Body

  test("Body.updated should do nothing for Empty quad trees") {
    val b1 = new Body(123f, 18f, 26f, 0f, 0f)
    val body = b1.updated(Empty(50f, 60f, 5f))

    assert(body.xspeed == 0f)
    assert(body.yspeed == 0f)
  }

  test("Body.updated should take bodies in a Leaf into account") {
    val b1 = new Body(123f, 18f, 26f, 0f, 0f)
    val b2 = new Body(524.5f, 24.5f, 25.5f, 0f, 0f)
    val b3 = new Body(245f, 22.4f, 41f, 0f, 0f)

    val quad = Leaf(15f, 30f, 20f, Seq(b2, b3))

    val body = b1.updated(quad)

    assert(body.xspeed ~= 12.587037f)
    assert(body.yspeed ~= 0.015557117f)
  }

  test("Body.updated should abstract far-away quadrants") {
    val b1 = new Body(123f, 1f, 2f, 0f, 0f)
    val b2 = new Body(524.5f, 24.5f, 25.5f, 0f, 0f)
    val b3 = new Body(245f, 25.4f, 26f, 0f, 0f)

    val leaf = Leaf(25f, 25f, 1f, Seq(b2, b3))
    val quad = Fork(leaf, Empty(26f, 25f, 1f), Empty(25f, 26f, 1f), Empty(26f, 26f, 1f))
    // mass = 769.5, massX = 24.7865497, massY = 25.65919428
    // distance = 33.549328169

    val body = b1.updated(quad)

    assert(body.xspeed ~= 0.48471746f)
    assert(body.yspeed ~= 0.48212218f)
  }

  // test cases for sector matrix

  test("'SectorMatrix.+=' should add a body at (25,47) to the correct bucket of a sector matrix of size 96") {
    val body = new Body(5, 25, 47, 0.1f, 0.1f)
    val boundaries = new Boundaries()
    boundaries.minX = 1
    boundaries.minY = 1
    boundaries.maxX = 97
    boundaries.maxY = 97
    val sm = new SectorMatrix(boundaries, SECTOR_PRECISION)
    sm += body
    val res = sm(2, 3).size == 1 && sm(2, 3).find(_ == body).isDefined
    assert(res, s"Body not found in the right sector")
  }

  test("Combining two sector matrices should yield a sectormatrix with the union of their bodies") {
    val b1 = new Body(123f, 1f, 2f, 0f, 0f)
    val b2 = new Body(524.5f, 26.5f, 25.5f, 0f, 0f)
    val b3 = new Body(245f, 25.4f, 26f, 0f, 0f)

    val boundaries = new Boundaries()
    boundaries.minX = 1
    boundaries.minY = 1
    boundaries.maxX = 97
    boundaries.maxY = 97

    val sm1 = new SectorMatrix(boundaries, SECTOR_PRECISION)
    sm1 += b1 += b3
    assert(sm1(0, 0).size == 1, "Sector (0, 0) should contain 1 body")
    assert(sm1(2, 2).size == 1, "Sector (2, 2) should contain 1 body")

    val sm2 = new SectorMatrix(boundaries, SECTOR_PRECISION)
    sm2 += b2
    assert(sm2(2, 2).size == 1, "Sector (2, 2) should contain 1 body")

    val sm3 = sm1.combine(sm2)
    assert(sm3(0, 0).size == 1, "Sector (0, 0) should contain 1 body")
    assert(sm3(2, 2).size == 2, "Sector (2, 2) should contain 2 bodies")
  }


  // Test cases for simulator

  test("updateBoundaries correctly updates boundaries") {
    val boundaries = new Boundaries
    boundaries.minX = 2.5f
    boundaries.minY = 2.5f
    boundaries.maxX = 23
    boundaries.maxY = 27

    val b1 = new Body(123f, 1f, 2f, 0f, 0f)
    val b2 = new Body(524.5f, 26.5f, 25.5f, 0f, 0f)
    val b3 = new Body(245f, 25.4f, 26f, 0f, 0f)

    val sim = new Simulator(defaultTaskSupport, new TimeStatistics)

    sim.updateBoundaries(boundaries, b1)
    assert(boundaries.minX == 1)
    assert(boundaries.minY == 2)
    assert(boundaries.maxX == 23)
    assert(boundaries.maxY == 27)

    sim.updateBoundaries(boundaries, b2)
    assert(boundaries.minX == 1)
    assert(boundaries.minY == 2)
    assert(boundaries.maxX == 26.5f)
    assert(boundaries.maxY == 27)

    sim.updateBoundaries(boundaries, b3)
    assert(boundaries.minX == 1)
    assert(boundaries.minY == 2)
    assert(boundaries.maxX == 26.5f)
    assert(boundaries.maxY == 27)
  }

  test("mergeBoundaries correctly merges") {
    val b1 = new Boundaries
    b1.minX = 1
    b1.minY = 2
    b1.maxX = 20
    b1.maxY = 25
    val b2 = new Boundaries
    b2.minX = 3
    b2.minY = 1
    b2.maxX = 10
    b2.maxY = 35

    val sim = new Simulator(defaultTaskSupport, new TimeStatistics)
    val b3 = sim.mergeBoundaries(b1, b2)
    assert(b3.minX == 1)
    assert(b3.minY == 1)
    assert(b3.maxX == 20)
    assert(b3.maxY == 35)
  }

  test("computeSectorMatrix gives a correct matrix") {
    val b1 = new Body(123f, 1f, 2f, 0f, 0f)
    val b2 = new Body(524.5f, 26.5f, 25.5f, 0f, 0f)
    val b3 = new Body(245f, 25.4f, 26f, 0f, 0f)

    val boundaries = new Boundaries()
    boundaries.minX = 1
    boundaries.minY = 1
    boundaries.maxX = 97
    boundaries.maxY = 97

    val sim = new Simulator(defaultTaskSupport, new TimeStatistics)
    val sm = sim.computeSectorMatrix(Seq(b1, b2, b3), boundaries)

    assert(sm(0, 0).size == 1, "Sector (0, 0) should contain 1 body")
    assert(sm(2, 2).size == 2, "Sector (2, 2) should contain 2 bodies")
  }

}

object FloatOps {
  private val precisionThreshold = 1e-4

  /** Floating comparison: assert(float ~= 1.7f). */
  implicit class FloatOps(val self: Float) extends AnyVal {
    def ~=(that: Float): Boolean =
      abs(self - that) < precisionThreshold
  }

  /** Long floating comparison: assert(double ~= 1.7). */
  implicit class DoubleOps(val self: Double) extends AnyVal {
    def ~=(that: Double): Boolean =
      abs(self - that) < precisionThreshold
  }

  /** Floating sequences comparison: assert(floatSeq ~= Seq(0.5f, 1.7f). */
  implicit class FloatSequenceOps(val self: Seq[Float]) extends AnyVal {
    def ~=(that: Seq[Float]): Boolean =
      self.size == that.size &&
        self.zip(that).forall { case (a, b) =>
          abs(a - b) < precisionThreshold
        }
  }
}

