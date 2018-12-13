package observatory


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import Math.{abs}
import Visualization._


@RunWith(classOf[JUnitRunner])
class VisualizationTest extends FunSuite with Checkers {

  test("Can compute distance") {
    assert(abs(distance(Location(0.0, 0.0), Location(1.0, 0.0)) - 111.0) < 1.0)
    assert(abs(distance(Location(60.0, 0.0), Location(61.0, 0.0)) - 111.0) < 1.0)
    assert(abs(distance(Location(0.0, 90.0), Location(1.0, 90.0)) - 111.0) < 1.0)

    assert(abs(distance(Location(0.0, 0.0), Location(0.0, 1.0)) - 111.0) < 1.0)
    assert(abs(distance(Location(40.0, 0.0), Location(40.0, 1.0)) - 85.0) < 1.0)
    assert(abs(distance(Location(-40.0, 100.0), Location(-40.0, 101.0)) - 85.0) < 1.0)

    assert(abs(distance(Location(52.6, 16.1), Location(-12.0, -145.7)) - 15207.0) < 10.0)
  }

  test("Can interpolate color") {
    assert(interpolateColor(Colors, 60.0) === Color(255, 255, 255))
    assertResult(Color(0, 0, 0)) { interpolateColor(Colors, -60.0) }
    assertResult(Color(0, 0, 0)) { interpolateColor(Colors, -600.0) }
    assertResult(Color(0, 255, 255)) { interpolateColor(Colors, 0.0) }
    assertResult(Color(128, 255, 128)) { interpolateColor(Colors, 6.0) }
  }

  test("[Test Description] [#2 - Raw data display] color interpolation") {
    assertResult(Color(191,0,64)) {
      val scale = List((-1.0,Color(255,0,0)), (0.0,Color(0,0,255)))
      interpolateColor(scale, -0.75)
    }
  }

  test("Can interpolate temperature") {
    val temps = Array[(Location, Double)] (
      (Location(-1.0, -1.0), 20.0),
      (Location(1.0, 1.0), 10.0)
    )

    assert(abs(predictTemperature(temps, Location(0.0, 0.0)) - 15.0) < 0.01)
  }
}
