package timeusage

import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, ColumnName, DataFrame, Row}
import org.apache.spark.sql.types.{DoubleType, StringType, StructField, StructType}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import timeusage.TimeUsage._

import scala.util.Random

@RunWith(classOf[JUnitRunner])
class TimeUsageSuite extends FunSuite with BeforeAndAfter { self =>

  var spark: SparkSession = _

  private object testImplicits extends SQLImplicits {
    protected override def _sqlContext: SQLContext = self.spark.sqlContext
  }
  import testImplicits._

  before {
    spark = SparkSession.builder().master("local").getOrCreate()
  }

  test("dfSchema") {
    val schema = TimeUsage.dfSchema("Col1" :: "Col2" :: "Col3" :: Nil)
    assert(schema.size === 3)
    assert(schema.head.name === "Col1")
    assert(schema.forall(_.nullable === false))
    assert(schema.head.dataType === StringType)
    assert(schema.last.name === "Col3")
    assert(schema.last.dataType === DoubleType)
  }

  test("Row") {
    val row = TimeUsage.row(List("Val1", "3.14", "5"))
    assert(row.size === 3)
    assert(row(0) === "Val1")

    assert(row(0).isInstanceOf[String])
    assert(row(1).isInstanceOf[Double])
    assert(row(2).isInstanceOf[Double])
  }

  test("ClassifiedColumns") {
    val (primaryNeeds, workingActivities, otherActivities) = TimeUsage.classifiedColumns(List(
      "t01", "t05", "t08", "t1801", "t1803", "t1866", "t1805", "t06"
    ))
    assert(primaryNeeds.size === 3)
    assert(workingActivities.size === 2)
    assert(otherActivities.size === 3)
  }

  test("Projection") {
    val (columns, initDf) = read("/timeusage/atussum.csv")

    val workingStatusProjection: Column = when(($"telfs" < 3 && $"telfs" >= 1), "working").otherwise("not working").as("workingStatus")
    val plusProjection: Column = List[Column]($"gemetsta", $"gtmetsta").reduce(_ + _)
    val df = initDf.select(workingStatusProjection, plusProjection)
    assert(df.columns.size === 2)
  }

  test("timeUsageSummary") {
    val (columns, initDf) = read("/timeusage/atussum.csv")
    val (primaryNeedsColumns, workColumns, otherColumns) = classifiedColumns(columns)
    val summaryDf = timeUsageSummary(primaryNeedsColumns, workColumns, otherColumns, initDf)

    summaryDf.show()
    assert(summaryDf.columns.size === 6)
  }

  test("timeUsageGrouped") {
    val (columns, initDf) = read("/timeusage/atussum.csv")
    val (primaryNeedsColumns, workColumns, otherColumns) = classifiedColumns(columns)
    val summaryDf = timeUsageSummary(primaryNeedsColumns, workColumns, otherColumns, initDf)
    val grouped = timeUsageGrouped(summaryDf)

    grouped.show()
    assert(grouped.count() === 12)
  }

  test("timeUsageGroupedSql") {
    val (columns, initDf) = read("/timeusage/atussum.csv")
    val (primaryNeedsColumns, workColumns, otherColumns) = classifiedColumns(columns)
    val summaryDf = timeUsageSummary(primaryNeedsColumns, workColumns, otherColumns, initDf)
    val grouped = timeUsageGroupedSql(summaryDf)

    grouped.show()
    assert(grouped.count() === 12)
  }

  test("timeUsageGroupedTyped") {
    val (columns, initDf) = read("/timeusage/atussum.csv")
    val (primaryNeedsColumns, workColumns, otherColumns) = classifiedColumns(columns)
    val summaryDf = timeUsageSummary(primaryNeedsColumns, workColumns, otherColumns, initDf)
    val summaryDS = timeUsageSummaryTyped(summaryDf)
    val grouped = timeUsageGroupedTyped(summaryDS)

    grouped.show()
    assert(grouped.count() === 12)
  }
}
