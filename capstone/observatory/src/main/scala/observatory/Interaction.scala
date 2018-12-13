package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import Math.{PI, atan, pow, sinh, toDegrees}
import java.io.File
import java.nio.file.Path

import observatory.Visualization.{interpolateColor, predictTemperature}

import scala.collection.immutable


/**
  * 3rd milestone: interactive visualization
  */
object Interaction {

  /**
    * @param zoom Zoom level
    * @param x    X coordinate
    * @param y    Y coordinate
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(zoom: Int, x: Int, y: Int): Location = {
    val n = pow(2, zoom)
    val lonDeg = x / n * 360.0 - 180.0
    val latRad = atan(sinh(PI * (1 - 2 * y / n)))
    val latDeg = latRad.toDegrees
    Location(latDeg, lonDeg)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors       Color scale
    * @param zoom         Zoom level
    * @param x            X coordinate
    * @param y            Y coordinate
    * @return A 256×256 image showing the contents of the tile defined by `x`, `y` and `zoom`
    */
  def tile(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)], zoom: Int, x: Int, y: Int): Image = {
    val coordinates = tileCoordinates(zoom, x, y)
    makeImage(coordinates, colors, predictTemperature(temperatures, _))
  }

  def tileCoordinates(zoom: Int, x: Int, y: Int): IndexedSeq[Location] = {
    for {
      row <- 0 to 255
      col <- 0 to 255
    } yield tileLocation(zoom + 8, x * 256 + col, y * 256 + row)
  }

  def makeImage(coordinates: IndexedSeq[Location], colors: Iterable[(Double, Color)], toColor: Location => Double): Image = {
    val pixels = coordinates.par.map { loc =>
      val temp = toColor(loc)
      val color = interpolateColor(colors, temp)
      Pixel(color.red, color.green, color.blue, 127)
    }

    Image(256, 256, pixels.toArray)
  }

  def makeAndWriteImage(year: Int, zoom: Int, x: Int, y: Int, temperatures: Iterable[(Location, Double)]): Unit = {
    val path = "target/temperatures/" + year + "/" + zoom
    val dir = new File(path)
    if (!dir.exists()) dir.mkdirs()

    val file = new File(path + "/" + x + "-" + y + ".png")
    if (!file.exists()) {
      Console.println("Computing tile " + file)

      val img = tile(temperatures, Visualization.Colors, zoom, x, y)
      img.output(file)
    }
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    *
    * @param yearlyData    Sequence of (year, data), where `data` is some data associated with
    *                      `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
                           yearlyData: Iterable[(Int, Data)],
                           generateImage: (Int, Int, Int, Int, Data) => Unit
                         ): Unit = {
    for {
      (year, temperatures) <- yearlyData
      zoom <- 0 to 3
      x <- 0 until 1 << zoom
      y <- 0 until 1 << zoom
    }
      generateImage(year, zoom, x, y, temperatures)
  }

}
