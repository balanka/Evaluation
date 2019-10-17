package com.iws.main
/*
import java.io.{File, InputStream}

import zio.{DefaultRuntime, IO, RIO, UIO, ZIO}

import scala.collection.mutable
import scala.util.Random

object MapSort {

  val numValues    = 10
  val ceilingValue = 100000

  def values: Vector[Int] = (1 to numValues).map(_ ⇒ Random.nextInt(ceilingValue)).toVector

  val randomMap: RIO[Parameters, mutable.Map[Int, Vector[Int]]] =
    ZIO.accessM[Parameters] { param ⇒
      ZIO.mergeAllPar((1 to param.mapSize).map(UIO(_))) {
        collection.mutable.Map.empty[Int, Vector[Int]]
      } { (mp, ind) =>
        mp += ind → values //mutate
        mp
      }
    }

  def sortConcurrentMap(
                         m: collection.mutable.Map[Int, Vector[Int]]
                       ): ZIO[Parameters, Nothing, (mutable.Map[Int, Vector[Int]], Double)] =
    ZIO.accessM[Parameters] { p ⇒
      for {
        // sort all keys concurrently with ZIO
        //  REF or what???
        //ZIO.par
        timeFirst ← UIO(System.nanoTime())
        _ ← ZIO.foreachPar_(m.keys) { k ⇒
          UIO {
            m.update(k, m(k).sorted)
            m
          }
        }
        timeLater ← UIO(System.nanoTime())
      } yield (m, (timeLater - timeFirst).toDouble / 1000000000.0)
    }

  def getAvgRuntime(
                     sorter: collection.mutable.Map[Int, Vector[Int]] ⇒ RIO[Parameters, (mutable.Map[Int, Vector[Int]], Double)]
                   ): RIO[Parameters, Double] = RIO.accessM[Parameters] { p ⇒
    val ts: RIO[Parameters, List[Double]] = RIO.foreach(1 to p.runs) { _ =>
      for {
        m ← randomMap
        t ← sorter(m).map(_._2)
      } yield t
    }
    ts.map(l ⇒ l.sum / p.runs)
  }

  val run: ZIO[Parameters, Throwable, Unit] = for {

    _   ← UIO(println("Running concurrent sort"))
    con ← getAvgRuntime(sortConcurrentMap)
    _   ← UIO(println(s"Concurrent time: $con"))

  } yield ()

  def readFile2(file: File): IO[Exception, List[Byte]] = {
    def readAll(is: InputStream, acc: List[Byte]): IO[Exception, List[Byte]] =
      is.read.flatMap {
        case None       => UIO.succeed(acc.reverse)
        case Some(byte) => readAll(is, byte :: acc)
      }

    ZIO.bracket(InputStream.openFile(file))(stream => stream.close.orDie){ stream =>
      readAll(stream, Nil)
    }
  }

  case class Parameters(mapSize: Int = 1000, runs: Int = 1, maxFiber: Int = 1)

  def main(args: Array[String]): Unit = {
    // this breaks with mapSize ~9000 or larger on the sortConcurrentMap, StackOverflowError
    val p = Parameters(mapSize = args(0).toInt, runs = args(1).toInt, maxFiber = args(2).toInt)
    println(s"RUNNING TEST with Parameters $p")
    val defaultRuntime = new DefaultRuntime {}
    defaultRuntime.unsafeRun(run.provide(p))
  }

}

 */