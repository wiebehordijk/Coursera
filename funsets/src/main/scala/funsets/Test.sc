import funsets.FunSets
import funsets.FunSets._

val s = union(singletonSet(45), singletonSet(999))
val m = map(s, x => 2*x)
FunSets.toString(m)
m(2*999)
