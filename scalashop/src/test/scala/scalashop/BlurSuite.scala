package scalashop

import java.util.concurrent._
import scala.collection._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common._

@RunWith(classOf[JUnitRunner])
class BlurSuite extends FunSuite {
  test("boxBlurKernel should correctly handle radius 0") {
    val src = new Img(5, 5)

    for (x <- 0 until 5; y <- 0 until 5)
      src(x, y) = rgba(x, y, x + y, math.abs(x - y))

    for (x <- 0 until 5; y <- 0 until 5)
      assert(boxBlurKernel(src, x, y, 0) === rgba(x, y, x + y, math.abs(x - y)),
        "boxBlurKernel(_,_,0) should be identity.")
  }

  test("boxBlurKernel should return the correct value on an interior pixel " +
    "of a 3x4 image with radius 1") {
    val src = new Img(3, 4)
    src(0, 0) = 0; src(1, 0) = 1; src(2, 0) = 2
    src(0, 1) = 3; src(1, 1) = 4; src(2, 1) = 5
    src(0, 2) = 6; src(1, 2) = 7; src(2, 2) = 8
    src(0, 3) = 50; src(1, 3) = 11; src(2, 3) = 16

    assert(boxBlurKernel(src, 1, 2, 1) === 12,
      s"(boxBlurKernel(1, 2, 1) should be 12, " +
        s"but it's ${boxBlurKernel(src, 1, 2, 1)})")

    assert(boxBlurKernel(src, 2, 3, 2) === 12,
      s"(boxBlurKernel(2, 3, 2) should be 12, " +
        s"but it's ${boxBlurKernel(src, 1, 2, 1)})")

    assert(boxBlurKernel(src, 2, 3, 3) === 9,
      s"(boxBlurKernel(2, 3, 3) should be 9, " +
        s"but it's ${boxBlurKernel(src, 1, 2, 1)})")
  }

  trait TestCases {
    val src3x3 = new Img(3, 3)
    val dst3x3 = new Img(3, 3)
    src3x3(0, 0) = 0; src3x3(1, 0) = 1; src3x3(2, 0) = 2
    src3x3(0, 1) = 3; src3x3(1, 1) = 4; src3x3(2, 1) = 5
    src3x3(0, 2) = 6; src3x3(1, 2) = 7; src3x3(2, 2) = 8

    val src4x3 = new Img(4, 3)
    val dst4x3 = new Img(4, 3)
    src4x3(0, 0) = 0; src4x3(1, 0) = 1; src4x3(2, 0) = 2; src4x3(3, 0) = 9
    src4x3(0, 1) = 3; src4x3(1, 1) = 4; src4x3(2, 1) = 5; src4x3(3, 1) = 10
    src4x3(0, 2) = 6; src4x3(1, 2) = 7; src4x3(2, 2) = 8; src4x3(3, 2) = 11
    
    def check3x3(x: Int, y: Int, expected: Int) =
      assert(dst3x3(x, y) === expected,
        s"(destination($x, $y) should be $expected)")

    def check4x3(x: Int, y: Int, expected: Int) =
      assert(dst4x3(x, y) === expected,
        s"(destination($x, $y) should be $expected)")

    def check3x3Image(): Unit = {
      check3x3(0, 0, 2)
      check3x3(1, 0, 2)
      check3x3(2, 0, 3)
      check3x3(0, 1, 3)
      check3x3(1, 1, 4)
      check3x3(2, 1, 4)
      check3x3(0, 2, 5)
      check3x3(1, 2, 5)
      check3x3(2, 2, 6)
    }

    def check4x3Image(): Unit = {
      check4x3(0, 0, 4)
      check4x3(1, 0, 5)
      check4x3(2, 0, 5)
      check4x3(3, 0, 6)
      check4x3(0, 1, 4)
      check4x3(1, 1, 5)
      check4x3(2, 1, 5)
      check4x3(3, 1, 6)
      check4x3(0, 2, 4)
      check4x3(1, 2, 5)
      check4x3(2, 2, 5)
      check4x3(3, 2, 6)
    }
  }


  test("HorizontalBoxBlur.blur with radius 1 should correctly blur part of the 3x3 image") {
    new TestCases {
      HorizontalBoxBlur.blur(src3x3, dst3x3, 0, 2, 1)

      check3x3(0, 0, 2)
      check3x3(1, 0, 2)
      check3x3(2, 0, 3)
      check3x3(0, 1, 3)
      check3x3(1, 1, 4)
      check3x3(2, 1, 4)
      check3x3(0, 2, 0)
      check3x3(1, 2, 0)
      check3x3(2, 2, 0)
    }
  }

  test("HorizontalBoxBlur.parBlur with radius 1 and 4 tasks should correctly blur the entire 3x3 image") {
    new TestCases {
      HorizontalBoxBlur.parBlur(src3x3, dst3x3, 4, 1)
      check3x3Image()
    }
  }

  test("HorizontalBoxBlur.parBlur with radius 1 and 32 tasks should correctly blur the entire 3x3 image") {
    new TestCases {
      HorizontalBoxBlur.parBlur(src3x3, dst3x3, 32, 1)
      check3x3Image()
    }
  }

  test("VerticalBoxBlur.blur with radius 2 should correctly blur the entire 4x3 image") {
    new TestCases {
      VerticalBoxBlur.blur(src4x3, dst4x3, 0, 4, 2)
      check4x3Image()
    }
  }

  test("VerticalBoxBlur.parBlur with radius 1 and 4 tasks should correctly blur the entire 3x3 image") {
    new TestCases {
      VerticalBoxBlur.parBlur(src3x3, dst3x3, 4, 1)
      check3x3Image()
    }
  }

  test("VerticalBoxBlur.parBlur with radius 1 and 32 tasks should correctly blur the entire 3x3 image") {
    new TestCases {
      VerticalBoxBlur.parBlur(src3x3, dst3x3, 32, 1)
      check3x3Image()
    }
  }

}
