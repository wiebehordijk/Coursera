package observatory

import java.time.LocalDate

//import scala.collection.mutable
import scala.io.Source

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

/**
  * 1st milestone: data extraction
  */
object Extraction {

  val conf: SparkConf = new SparkConf().setMaster("local").setAppName("Observatory")
  val sc: SparkContext = new SparkContext(conf)

  import org.apache.log4j.{Level, Logger}

  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Int, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Double)] = {
    val stations = readStationMap(stationsFile)
    val temperatureStream = getClass.getResourceAsStream(temperaturesFile)
    val tempLines = Source.fromInputStream(temperatureStream).getLines
    val values = tempLines.map {
      _.split(",")
    }.toSeq

    values filter { v => v(4) != "9999.9" && stations.contains((v(0), v(1))) } map
      { v => (LocalDate.of(year, v(2).toInt, v(3).toInt), stations((v(0), v(1))), toCelcius(v(4).toDouble)) }
  }

  def toCelcius(fahrenheit: Double): Double = (fahrenheit - 32.0) / 1.8

  def readStationMap(stationsFile: String): Map[(String, String), Location] = {
    val stationStream = getClass.getResourceAsStream(stationsFile)
    val stationLines = Source.fromInputStream(stationStream).getLines.toList
    val usable = stationLines map {
      _.split(',')
    } filter { l => l.size == 4 && l(2) != "" && l(3) != "" }

    usable.map(u => (u(0), u(1)) -> Location(u(2).toDouble, u(3).toDouble)).toMap
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
//  def locationYearlyAverageRecordsFast(records: Iterable[(LocalDate, Location, Double)]): Iterable[(Location, Double)] = {
//    val sumTemp = new mutable.HashMap[Location, (Double, Long)]()
//    for ((date, location, temperature) <- records) {
//      if (sumTemp.contains(location))
//        sumTemp(location) = (sumTemp(location)._1 + temperature, (sumTemp(location)._2 + 1))
//      else
//        sumTemp(location) = (temperature, 1)
//    }
//
//    sumTemp map { case (location, sums) => (location, sums._1 / sums._2) }
//  }


  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Double)]): Iterable[(Location, Double)] = {
    val rdd = sc.parallelize(records.map(t => (t._2, (t._3, 1))).toList)
    val sums = rdd.reduceByKey((a, b) => (a._1 + b._1, a._2 + b._2))
    val avgs = sums.map { case (k: Location, (s: Double, c: Int)) => (k, s / c) }
    avgs.collect.toList
  }

}
