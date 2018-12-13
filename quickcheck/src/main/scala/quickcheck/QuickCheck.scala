package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  lazy val genHeap: Gen[H] = {
    def addToHeap(heap: H, values: List[A]): H = values match {
      case Nil => heap
      case x :: xs => addToHeap(insert(x, heap), xs)
    }

    Arbitrary.arbitrary[List[A]] map (addToHeap(empty, _))
  }

  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)

  property("gen1") = forAll { (h: H) =>
    val m = if (isEmpty(h)) 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  val propInsertEmpty = forAll { (a: A) =>
    val h = insert(a, empty)
    (findMin(h) == a)         :| "findMin(insert(a, empty)) == a" &&
      (deleteMin(h) == empty)   :| "insert and delete in empty yields empty"
  }

  val propInsertTwo = forAll { (a: A, b: A) =>
    val h = insert(a, insert(b, empty))
    (findMin(h) == ord.min(a, b))   :| "after inserting two values in empty, findMin must return lowest value"
  }

  val propSorted = forAll { (h: H) =>
    val l = toList(h)
    (l.sorted == l)     :| "must always return lowest value"
  }

  val propMeldMin = forAll { (h1: H, h2: H) =>
    val hm = meld(h1, h2)
    (!isEmpty(h1) && !isEmpty(h2)) ==> (findMin(hm) == ord.min(findMin(h1), findMin(h2)))
  }

  val propMeldCompareLists = forAll { (h1: H, h2: H) =>
    val l1 = toList(h1)
    val l2 = toList(h2)
    val hm = meld(h1, h2)
    val lm = toList(hm)
    (lm == (l1 ++ l2).sorted)    :| "melding two heaps should give the same list as the original heaps"
  }

  property("insertEmpty") = propInsertEmpty
  property("insertTwo") = propInsertTwo
  property("sorted") = propSorted
  property("melding findMin") = propMeldMin
  property("melding compare lists") = propMeldCompareLists
}
