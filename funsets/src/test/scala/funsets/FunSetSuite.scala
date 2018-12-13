package funsets

import org.scalatest.FunSuite


import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * This class is a test suite for the methods in object FunSets. To run
 * the test suite, you can either:
 *  - run the "test" command in the SBT console
 *  - right-click the file in eclipse and chose "Run As" - "JUnit Test"
 */
@RunWith(classOf[JUnitRunner])
class FunSetSuite extends FunSuite {

  /**
   * Link to the scaladoc - very clear and detailed tutorial of FunSuite
   *
   * http://doc.scalatest.org/1.9.1/index.html#org.scalatest.FunSuite
   *
   * Operators
   *  - test
   *  - ignore
   *  - pending
   */

  /**
   * Tests are written using the "test" operator and the "assert" method.
   */
  // test("string take") {
  //   val message = "hello, world"
  //   assert(message.take(5) == "hello")
  // }

  /**
   * For ScalaTest tests, there exists a special equality operator "===" that
   * can be used inside "assert". If the assertion fails, the two values will
   * be printed in the error message. Otherwise, when using "==", the test
   * error message will only say "assertion failed", without showing the values.
   *
   * Try it out! Change the values so that the assertion fails, and look at the
   * error message.
   */
  // test("adding ints") {
  //   assert(1 + 2 === 3)
  // }


  import FunSets._

  test("contains is implemented") {
    assert(contains(x => true, 100))
  }

  /**
   * When writing tests, one would often like to re-use certain values for multiple
   * tests. For instance, we would like to create an Int-set and have multiple test
   * about it.
   *
   * Instead of copy-pasting the code for creating the set into every test, we can
   * store it in the test class using a val:
   *
   *   val s1 = singletonSet(1)
   *
   * However, what happens if the method "singletonSet" has a bug and crashes? Then
   * the test methods are not even executed, because creating an instance of the
   * test class fails!
   *
   * Therefore, we put the shared values into a separate trait (traits are like
   * abstract classes), and create an instance inside each test method.
   *
   */

  trait TestSets {
    val s1 = singletonSet(1)
    val s2 = singletonSet(2)
    val s3 = singletonSet(3)
    val s12 = union(s1, s2)
    val s23 = union(s2, s3)
    val s13 = union(s1, s3)
    val s123 = union(s12, s3)
  }

  // Helper function to check if a set contains exactly the values expected
  def onlyContains(s: Set, elems: List[Int]) = 
    !(-1000 to 1000).exists((x: Int) => s(x) && !elems.contains(x)) && 
    elems.forall(contains(s, _))


  test("singletonSet(1) only contains 1") {

    /**
     * We create a new instance of the "TestSets" trait, this gives us access
     * to the values "s1" to "s3".
     */
    new TestSets {
      /**
       * The string argument of "assert" is a message that is printed in case
       * the test fails. This helps identifying which assertion failed.
       */
      assert(onlyContains(s1, List(1)), "Singleton")
    }
  }

  test("union contains all elements of each set") {
    new TestSets {
      assert(onlyContains(s12, List(1, 2)), "Union only contains 1 and 2")
    }
  }

  test("intersect contains all elements that occur in both sets") {
    new TestSets {
      val empty = intersect(s1, s2)
      assert(onlyContains(empty, List()), "Intersection of s1 and s2 is empty")

      val s = intersect(s12, s23)
      assert(onlyContains(s, List(2)), "Intersection only contains 2")
    }
  }

  test("diff contains all elements that occur in set 1 but not in set 2") {
    new TestSets {
      val dif1 = diff(s1, s2)
      assert(onlyContains(dif1, List(1)), "Diff between disjunct sets")

      val dif2 = diff(s123, s1)
      assert(onlyContains(dif2, List(2, 3)), "Diff only contains 2 and 3")
    }
  }

  test("filter contains odd numbers") {
    new TestSets {
      val f1 = filter(s123, (_ % 2 != 0))
      assert(onlyContains(f1, List(1, 3)), "Filter only contains 1 and 3")
    }
  }


  test("forall") {
    new TestSets {
      assert(forall(s1, _ == 1), "All elements of s1 are 1")
      assert(!forall(s1, _ != 1), "All elements of s1 are 1")
      assert(forall(s123, _ > 0), "All elements of s123 are > 0")
    }
  }

  test("exists") {
    new TestSets {
      assert(exists(s1, _ == 1), "At least one element of s1 is 1")
      assert(!exists(s1, _ != 1), "All elements of s1 are 1")
      assert(exists(s123, _ > 0), "At least one element of s123 is > 0")
    }
  }

  test("map") {
    new TestSets {
      val smin12 = map(s12, -_)
      assert(onlyContains(smin12, List(-1, -2)), "smin12 only contains -1 and -2")

      val s246 = map(s123, _ * 2)
      assert(onlyContains(s246, List(2, 4, 6)), "s246 only contains 2, 4, 6")
    }
  }

}
