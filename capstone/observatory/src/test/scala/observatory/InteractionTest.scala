package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import org.junit.Assert._
import scala.collection.concurrent.TrieMap

import Interaction._

@RunWith(classOf[JUnitRunner])
class InteractionTest extends FunSuite with Checkers {

  def assertLocationEquals(expected: Location, actual: Location, delta: Double): Unit = {
    if (Math.abs(expected.lat - actual.lat) > delta || Math.abs(expected.lon - actual.lon) > delta)
      fail("\nExpected: " + expected + "\nActual: " + actual)
  }

  test("Can compute Location from tile") {
    assertLocationEquals(Location(85.0511, -180.0), tileLocation(0, 0, 0), 0.001)
    assertLocationEquals(Location(85.0511, -180.0), tileLocation(1, 0, 0), 0.001)
    assertLocationEquals(Location(85.0511, -180.0), tileLocation(2, 0, 0), 0.001)
    assertLocationEquals(Location(85.0511, -180.0), tileLocation(3, 0, 0), 0.001)
    assertLocationEquals(Location(85.0511, -180.0), tileLocation(4, 0, 0), 0.001)

    assertLocationEquals(Location(0.0, 0.0), tileLocation(1, 1, 1), 0.001)

  }
}
