abstract class Nat {
  def isZero: Boolean
  def predecessor: Nat
  def successor: Nat
  def + (that: Nat): Nat
  def - (that: Nat): Nat
}

object Zero extends Nat {
  def isZero: Boolean = true

  def predecessor: Nat = throw new IllegalArgumentException

  def successor: Nat = new Succ(this)

  def +(that: Nat): Nat = that

  def -(that: Nat): Nat = if (that.isZero) this else throw new IllegalArgumentException

  override def toString: String = "Zero"
}


class Succ(n: Nat) extends Nat {
  def isZero: Boolean = false

  def predecessor: Nat = n

  def successor: Nat = new Succ(this)

  def +(that: Nat): Nat = predecessor + that.successor

  def -(that: Nat): Nat = if (that.isZero) this else predecessor - that.predecessor

  override def toString: String = "Succ(" + predecessor + ")"
}
