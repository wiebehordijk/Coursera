import objsets._

val tw1 = new Tweet("wiebe", "hallo", 0)
val tw2 = new Tweet("wiebe", "bla", 3)

val ts1 = new NonEmpty(tw1, new Empty, new Empty)
val ts2 = new NonEmpty(tw2, new Empty, new Empty)
val ts3 = ts1.union(ts2)
val ts4 = ts1.union(ts3)
ts4.foreach(t => println(t.toString))

val tl1 = ts4.descendingByRetweet

tl1.foreach(t => println(t.toString))
tl1.isEmpty

GoogleVsApple.trending foreach println
