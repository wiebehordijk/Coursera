
import common._

import scala.collection.immutable.IndexedSeq

package object scalashop {

  /** The value of every pixel is represented as a 32 bit integer. */
  type RGBA = Int

  /** Returns the red component. */
  def red(c: RGBA): Int = (0xff000000 & c) >>> 24

  /** Returns the green component. */
  def green(c: RGBA): Int = (0x00ff0000 & c) >>> 16

  /** Returns the blue component. */
  def blue(c: RGBA): Int = (0x0000ff00 & c) >>> 8

  /** Returns the alpha component. */
  def alpha(c: RGBA): Int = (0x000000ff & c) >>> 0

  /** Used to create an RGBA value from separate components. */
  def rgba(r: Int, g: Int, b: Int, a: Int): RGBA = {
    (r << 24) | (g << 16) | (b << 8) | (a << 0)
  }

  /** Restricts the integer into the specified range. */
  def clamp(v: Int, min: Int, max: Int): Int = {
    if (v < min) min
    else if (v > max) max
    else v
  }

  /** Image is a two-dimensional matrix of pixel values. */
  class Img(val width: Int, val height: Int, private val data: Array[RGBA]) {
    def this(w: Int, h: Int) = this(w, h, new Array(w * h))
    def apply(x: Int, y: Int): RGBA = data(y * width + x)
    def update(x: Int, y: Int, c: RGBA): Unit = data(y * width + x) = c
  }

  /** Computes the blurred RGBA value of a single pixel of the input image. */
  def boxBlurKernel(src: Img, x: Int, y: Int, radius: Int): RGBA = {
    val minx = clamp(x - radius, 0, src.width-1)
    val maxx = clamp(x + radius, 0, src.width-1)
    val miny = clamp(y - radius, 0, src.height-1)
    val maxy = clamp(y + radius, 0, src.height-1)

    var yy = miny
    var num = 0
    var sumRed, sumGreen, sumBlue, sumAlpha = 0

    while (yy <= maxy) {
      var xx = minx
      while (xx <= maxx) {
        num += 1
        val color = src(xx, yy)
        sumRed += red(color)
        sumGreen += green(color)
        sumBlue += blue(color)
        sumAlpha += alpha(color)
        xx += 1
      }
      yy += 1
    }

    rgba(sumRed / num, sumGreen / num, sumBlue / num, sumAlpha / num)
  }

  /** Computes a list of start-end tuples for a given range and a number of tuples */
  def startEndTuples(end: Int, num: Int): IndexedSeq[(RGBA, RGBA)] = {
    // If num is large, step could be 0 which gives an exception. Step must be 1 at least
    val step = (end / num) max 1
    val boundaries = (0 until end by step) :+ end
    boundaries zip boundaries.tail
  }

}
