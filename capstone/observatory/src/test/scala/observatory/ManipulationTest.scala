package observatory

import java.lang.Math.abs

import observatory.Visualization.predictTemperature
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import org.junit.Assert._

@RunWith(classOf[JUnitRunner])
class ManipulationTest extends FunSuite with Checkers {

  val temps1 = List[(Location, Double)] (
    (Location(-1.0, -1.0), 20.0),
    (Location(1.0, 1.0), 10.0)
  )
  val temps2 = List[(Location, Double)] (
    (Location(-1.0, -1.0), 30.0),
    (Location(1.0, 1.0), 20.0)
  )

  test("Can make grid") {
    def grid = Manipulation.makeGrid(temps1)
    assertEquals(20.0, grid(-1, -1), 0.01)
    assertEquals(10.0, grid(1, 1), 0.01)
    assertEquals(15.0, grid(0, 0), 0.01)
  }

  test("Can interpolate temperature") {
    def averages = Manipulation.average(List[List[(Location, Double)]] (temps1, temps2))
    assertEquals(20.0, averages(0, 0), 0.01)
    assertEquals(15.0, averages(1, 1), 0.01)
    assertEquals(25.0, averages(-1, -1), 0.01)
  }
}