trait Expr

case class Val(n: Int) extends Expr

case class Sum(left: Expr, right: Expr) extends Expr

case class Prod(left: Expr, right: Expr) extends Expr

def show(e: Expr): String = showPred(e, 0)

def showPred(e: Expr, precedence: Int): String = {
  e match {
    case Val(n) => n.toString
    case Prod(l, r) => showPred(l, 1) + " * " + showPred(r, 1)
    case Sum(l, r) if precedence == 0 => showPred(l, 0) + " + " + showPred(r, 0)
    case Sum(l, r) if precedence > 0 => "(" + showPred(l, 0) + " + " + showPred(r, 0) + ")"
  }
}

val e1 = Sum(Val(4), Val(18))
val e2 = Prod(Val(4), Val(8))
val e3 = Sum(Prod(Val(3), Val(9)), Val(8))
val e4 = Prod(Sum(Val(3), Val(9)), Val(8))
show(e1)
show(e2)
show(e3)
show(e4)
