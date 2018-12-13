object Newton {

  def deriv(f: Double => Double): Double => Double = {
    val dx = 0.0001
    x => (f(x + dx) - f(x)) / dx
  }

  def halfInterval(f: Double => Double, a: Double, b: Double): Double = {
    def closeEnough(x: Double, y: Double) = {
      (x - y).abs < 0.0001
    }
    def search(a: Double, b: Double): Double = {
      println("search " + a + ", " + b)
      val mid = (a + b) / 2
      if (closeEnough(a, b)) mid
      else {
        val test = f(mid)
        if (test > 0) search(a, mid)
        else if (test < 0) search(mid, b)
        else mid
      }
    }

    if (f(a) < 0 && f(b) > 0) search(a, b)
    else if (f(a) > 0 && f(b) < 0) search(b, a)
    else throw new IllegalArgumentException("One end must be negative and one positive")
  }

  halfInterval(x => 2*x - 5, 0, 10)
}