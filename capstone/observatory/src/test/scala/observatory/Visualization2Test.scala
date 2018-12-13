package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import org.junit.Assert._

@RunWith(classOf[JUnitRunner])
class Visualization2Test extends FunSuite with Checkers {

  test("Can do bilinear interpolation") {
    assertEquals(2.875, Visualization2.bilinearInterpolation(0.25, 0.25, 1.0, 5.0, 4.0, 10.0), 0.01)
  }

}
