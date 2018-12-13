package observatory

import com.sksamuel.scrimage.{Image, Pixel}


/**
  * 2nd milestone: basic visualization
  */
object Visualization {

  val Colors = Array[(Double, Color)](
    (60.0, Color(255, 255, 255)),
    (32.0, Color(255, 0, 0)),
    (12.0, Color(255, 255, 0)),
    (0.0, Color(0, 255, 255)),
    (-15.0, Color(0, 0, 255)),
    (-27.0, Color(255, 0, 255)),
    (-50.0, Color(33, 0, 107)),
    (-60.0, Color(0, 0, 0))
  )

  val MeanEarthRadius = 6371.0 // in Km

  def distance(loc1: Location, loc2: Location): Double = {

    import org.apache.commons.math3.util.FastMath._

    val dSig: Double = acos(sin(loc1.lat.toRadians) * sin(loc2.lat.toRadians) +
      cos(loc1.lat.toRadians) * cos(loc2.lat.toRadians) * cos((loc1.lon - loc2.lon).toRadians))
    dSig * MeanEarthRadius
  }

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location     Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Double)], location: Location): Double = {

    import Math.{pow}

    val p = 2.0

    val distances = for {
      (station, temp) <- temperatures
    } yield (distance(station, location), temp)

    if (distances.exists(_._1 < 1.0)) // If distance < 1Km, take temperature of station
      distances.filter(_._1 < 1.0).head._2
    else {
      val (sumWU, sumW) = distances.aggregate(0.0, 0.0)({
        case ((swu, sw), (d, t)) =>
          val w = 1.0 / pow(d, p)
          (swu + w * t, sw + w)
      }, { case ((d1, t1), (d2, t2)) => (d1 + d2, t1 + t2) })
      sumWU / sumW
    }

  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value  The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Double, Color)], value: Double): Color = {
    val (lower, higher) = points.partition(_._1 < value)
    if (lower.isEmpty) higher.minBy(_._1)._2
    else if (higher.isEmpty) lower.maxBy(_._1)._2
    else {
      val under = lower.maxBy(_._1)
      val over = higher.minBy(_._1)
      if (over._1 == value) over._2
      else {
        val wu = 1.0 - (value - under._1) / (over._1 - under._1)
        val wo = 1.0 - (over._1 - value) / (over._1 - under._1)
        Color(
          ((under._2.red * wu + over._2.red * wo) / (wu + wo)).round.toInt,
          ((under._2.green * wu + over._2.green * wo) / (wu + wo)).round.toInt,
          ((under._2.blue * wu + over._2.blue * wo) / (wu + wo)).round.toInt
        )
      }
    }
  }

  /**
    * @param temperatures Known temperatures
    * @param colors       Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)]): Image = {
    val coordinates = for {
      lat <- 90 to -89 by -1
      lon <- -180 to 179
    } yield (lat, lon)

    val pixels = coordinates.par.map { case (lat, lon) =>
      val temp = predictTemperature(temperatures, Location(lat, lon))
      val color = interpolateColor(colors, temp)
      Pixel(color.red, color.green, color.blue, 255)
    }

    Image(360, 180, pixels.toArray)
  }

}

