import objsets.{Empty, NonEmpty, Tweet}

val tw1 = new Tweet("wiebe", "hallo", 0)
val tw2 = new Tweet("wiebe", "bla", 3)

val ts1 = new NonEmpty(tw1, new Empty, new Empty)
ts1
