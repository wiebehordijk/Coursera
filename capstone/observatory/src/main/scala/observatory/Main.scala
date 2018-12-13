package observatory

import java.io.File


object Main extends App {

  override def main(args: Array[String]): Unit = {
    //    val img = visualize(averages, Visualization.Colors)
    //    img.output("test.png")

//    for (year <- 1975 to 2015) {
//      val temperatures = Extraction.locateTemperatures(year, "/stations.csv", "/" + year + ".csv")
//      val averages = Extraction.locationYearlyAverageRecordsFast(temperatures)
//      Console.println("Computed " + averages.size + " averages")
//
//      generateTiles(
//        Array((year, averages)),
//        Interaction.makeAndWriteImage
//      )
//    }

    val temperaturess = for {
      year <- 1975 to 1989
      temperatures = Extraction.locateTemperatures(year, "/stations.csv", "/" + year + ".csv")
      averages = Extraction.locationYearlyAverageRecords(temperatures)
    } yield {
      Console.println("Computed " + averages.size + " averages for year " + year)
      averages
    }.toSeq

    val normals = Manipulation.average(temperaturess)
    Console.println("normals calculated")

    for (year <- 1990 to 2015) {
      val temperatures = Extraction.locateTemperatures(year, "/stations.csv", "/" + year + ".csv")
      val averages = Extraction.locationYearlyAverageRecords(temperatures)
      Console.println("Computed " + averages.size + " averages for year " + year)

      val deviation = Manipulation.deviation(averages, normals)

      Interaction.generateTiles(
        List((year, deviation)),
        makeAndWriteImage
      )
    }
  }


  def makeAndWriteImage(year: Int, zoom: Int, x: Int, y: Int, deviation: (Int, Int) => Double): Unit = {
    val path = "target/deviations/" + year + "/" + zoom
    val dir = new File(path)
    if (!dir.exists()) dir.mkdirs()

    val file = new File(path + "/" + x + "-" + y + ".png")
    if (!file.exists()) {
      Console.println("Computing tile " + file)

      val img = Visualization2.visualizeGrid(deviation, Visualization2.Colors, zoom, x, y)
      img.output(file)
    }
  }
}
