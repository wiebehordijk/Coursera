package observatory

import observatory.Extraction.getClass
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.io.Source

@RunWith(classOf[JUnitRunner])
class ExtractionTest extends FunSuite {

  test("Can read stations file") {
    val stations = Extraction.readStationMap("/stations.csv")

    assert(stations.size === 28128)
    assert(stations(("007018", "")) === Location(0.0, 0.0))
  }

  test("Can open iterator multiple times") {
    val stations = Extraction.readStationMap("/stations.csv")
    stations.iterator.toList
    assert(stations.iterator.next() != null)
  }

  test("Can read temperatures file") {
    val temperatures = Extraction.locateTemperatures(1975, "/stations.csv", "/1975.csv")

    val first1 = temperatures.take(1)
    assert(first1.forall(t => t._2 === Location(70.933, -8.667)))
  }

  test("Can convert to celcius") {
    assert(Math.abs(Extraction.toCelcius(29.037) - -1.646111) < 0.001)
  }

  test("Can compute averages") {
    val temperatures = Extraction.locateTemperatures(1975, "/stations.csv", "/1975.csv")
    val averages = Extraction.locationYearlyAverageRecords(temperatures)
    val avMap = averages.toMap

    //assert(averages.size === 28128)
    val avTest = avMap(Location(70.933, -8.667))
    assert(Math.abs(avTest - -1.646111) < 0.001)

  }
}