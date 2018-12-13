import scalashop._

VerticalBoxBlur.startEndTuples(0, 90, 10)

val src = new Img(3, 4)
src(0, 0) = 0; src(1, 0) = 1; src(2, 0) = 2
src(0, 1) = 3; src(1, 1) = 4; src(2, 1) = 5
src(0, 2) = 6; src(1, 2) = 7; src(2, 2) = 8
src(0, 3) = 50; src(1, 3) = 11; src(2, 3) = 16

boxBlurKernel(src, 2, 3, 3)
