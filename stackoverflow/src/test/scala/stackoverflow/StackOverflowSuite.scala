package stackoverflow

import org.scalatest.{FunSuite, BeforeAndAfterAll}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import java.io.File

@RunWith(classOf[JUnitRunner])
class StackOverflowSuite extends FunSuite with BeforeAndAfterAll {

  val q1 = Posting(1, 123, None, None, 5, Option("Java"))
  val q2 = Posting(1, 124, None, None, 15, Option("C#"))

  lazy val testObject = new StackOverflow {
    override val langs =
      List(
        "JavaScript", "Java", "PHP", "Python", "C#", "C++", "Ruby", "CSS",
        "Objective-C", "Perl", "Scala", "Haskell", "MATLAB", "Clojure", "Groovy")
    override def langSpread = 50000
    override def kmeansKernels = 45
    override def kmeansEta: Double = 20.0D
    override def kmeansMaxIterations = 120
  }

  test("testObject can be instantiated") {
    val instantiatable = try {
      testObject
      true
    } catch {
      case _: Throwable => false
    }
    assert(instantiatable, "Can't instantiate a StackOverflow object")
  }


  test("GroupedPostings for empty RDD") {
    val rdd = StackOverflow.sc.parallelize(List[Posting]())
    val result = testObject.groupedPostings(rdd).collect()
    assert(result.isEmpty)
  }

  test("GroupedPostings for small RDD") {
    val input = List(
      Posting(1, 123, None, None, 5, None),
      Posting(1, 124, None, None, 5, None),
      Posting(2, 125, None, Some(124), 5, None),
      Posting(2, 126, None, Some(124), 5, None)
    )
    val rdd = StackOverflow.sc.parallelize(input)
    val result = testObject.groupedPostings(rdd).collectAsMap()
    assert(result.size === 1)
    assert(result(124).size === 2)
  }

  test("scoredPostings for empty RDD") {
    val rdd = StackOverflow.sc.parallelize(List[(Int, Iterable[(Posting, Posting)])]())
    val result = testObject.scoredPostings(rdd).collect()
    assert(result.isEmpty)
  }

  test("scoredPostings for small RDD") {
    val input = List[(Int, Iterable[(Posting, Posting)])](
      (q1.id, Seq(
        (q1, Posting(2, 125, None, Some(123), 5, None)),
        (q1, Posting(2, 126, None, Some(123), 34, None)),
        (q1, Posting(2, 127, None, Some(123), 24, None))
      )),
      (q2.id, Seq(
        (q2, Posting(2, 128, None, Some(124), 50, None)),
        (q2, Posting(2, 129, None, Some(124), 3, None)),
        (q2, Posting(2, 130, None, Some(124), 2, None))
      ))
    )
    val rdd = StackOverflow.sc.parallelize(input)
    val result = testObject.scoredPostings(rdd).collectAsMap()
    assert(result.size === 2)
    assert(result(q1) === 34)
    assert(result(q2) === 50)
  }

  test("vectorPostings for empty RDD") {
    val rdd = StackOverflow.sc.parallelize(List[(Posting, Int)]())
    val result = testObject.vectorPostings(rdd)
    assert(result.isEmpty())
  }

  test("vectorPostings for small RDD") {
    val input = List(
      (q1, 46),
      (q2, 12)
    )
    val rdd = StackOverflow.sc.parallelize(input)
    val result = testObject.vectorPostings(rdd).collectAsMap()
    assert(result.size === 2)
    assert(result(1 * StackOverflow.langSpread) === 46)
    assert(result(4 * StackOverflow.langSpread) === 12)
  }


  test("clusterResults"){
    val centers = Array((0,0), (100000, 0))
    val rdd = StackOverflow.sc.parallelize(List(
      (0, 1000),
      (0, 23),
      (0, 234),
      (0, 0),
      (0, 1),
      (0, 1),
      (50000, 2),
      (50000, 10),
      (100000, 2),
      (100000, 5),
      (100000, 10),
      (200000, 100)  ))
    testObject.printResults(testObject.clusterResults(centers, rdd))
  }
}
