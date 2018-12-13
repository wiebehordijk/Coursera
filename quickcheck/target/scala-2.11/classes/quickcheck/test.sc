import quickcheck._

object QuickCheckBinomialHeap extends QuickCheckHeap with Bogus5BinomialHeap
import QuickCheckBinomialHeap._
val sample = genHeap.sample.get
toList(sample)

propInsertEmpty.check
propInsertTwo.check
propSorted.check
propMeldMin.check
propMeldSingletons.check
propMeldCompareLists.check
val h1 = insert(1, empty)
val h2 = insert(2, empty)
val hm = meld(h1, h2)
isEmpty(meld(empty, empty))
