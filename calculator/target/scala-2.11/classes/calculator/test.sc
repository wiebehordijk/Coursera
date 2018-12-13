import calculator.{Signal, Polynomial, Var}
import Polynomial._

val a = Signal(3)
val b = Signal(a() + 1)
b()
def se(x: Double) = Signal(x)
val c = se(b())
c()
