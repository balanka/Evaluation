package com.iws.main
import zio.{DefaultRuntime, UIO, IO}
/*
class Callbacks[A, E, CE] {
  // callbacks are all returning Units with a generic callback error `CE` type.
  type READER = IO[E, (Int, IO[CE, Unit])]
  def reader1(): READER = ???
  def reader2(): READER = ???
  def reader3(): READER = ???

  // manager function call readers, does its business decision algo and return according callback
  def manager(readers: List[READER]): READER = {
    // default callback (if no readers // no reader with positive value) does nothing
    val defaultReader: IO[E, (Int, IO[CE, Unit])] = (0, UIO.unit).succeed
    // loop over reader to find the one with the highest score - here, it's just value, nothing is executed
    readers.foldLeft(defaultReader) { case (current, next) =>
      current.zipWith(next) { case ((cr, cc), (nr, nc)) =>
        if(cr > nr) (cr, cc) else (nr, nc)
      }
    }
  }


  val readWriteProg = for {
    chosen  <- manager(List(reader1(), reader2(), reader3()))
    _       <- IO.effect(println(s"Max value found: ${chosen._1}"))
    written <- chosen._2 // exec selected callback
  } yield ()

  def main(args: Array[String]): Unit = {

    new DefaultRuntime(){}.unsafeRunSync(readWriteProg)

  }
}

 */