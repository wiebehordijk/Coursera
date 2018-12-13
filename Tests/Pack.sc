object Packer {

  def pack[T](xs: List[T]): List[List[T]] = xs match {
    case Nil => Nil
    case x :: xs1 => {
      val (first, rest) = xs span (_ == x)
      first :: pack(rest)
    }
  }

  val packed = pack(List("a", "a", "a", "b", "c", "c", "a"))

  def encodePack[T](xs: List[List[T]]): List[(T, Int)] = xs match {
    case Nil => Nil
    case first :: rest =>
      (first.head, first.length) :: encodePack(rest)
  }

  encodePack(packed)

  def encode[T](xs: List[T]): List[(T, Int)] = {
    pack(xs) map (ys => (ys.head, ys.length))
  }

  encode(List("a", "a", "a", "b", "c", "c", "a"))

  def mapFun[T, U](xs: List[T], f: T => U): List[U] =
    (xs foldRight List[U]())( f(_) :: _ )

  mapFun[Int, Int](List(1,5,-4), 2 * _)

  def lengthFun[T](xs: List[T]): Int =
    (xs foldRight 0)( (x, i) => 1 + i )

  lengthFun(List(1,5,-4))

  val map = Map((1, "A"), (2, "B"), (3, "C"))
  map.withDefaultValue("X")(5)

}