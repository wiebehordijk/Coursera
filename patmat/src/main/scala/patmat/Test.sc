import patmat.Huffman._

val t1 = Leaf('a', 5)
val t2 = Leaf('b', 8)
val t3 = Fork(t1, t2, List('c', 'd'), 10)
weight(t3)
chars(t3)

val sampleTree = makeCodeTree(
  makeCodeTree(Leaf('x', 1), Leaf('e', 1)),
  Leaf('t', 2)
)

val counts = times("De aardige pers".toList)
val leafs = makeOrderedLeafList(counts)
val com1 = combine(leafs)
val comn = until(singleton, combine)(leafs)

val tree = createCodeTree("De aardige pers".toList)

val text = decode(frenchCode, secret)
val sec1 = encode(frenchCode)("huffman".toList)
convert(frenchCode)
quickEncode(frenchCode)("huffman".toList)
