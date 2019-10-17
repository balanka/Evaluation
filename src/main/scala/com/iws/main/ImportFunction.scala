package com.iws.main

import java.io.File
import java.nio.charset.CharsetDecoder

import com.iws.model._

trait  ImportFunction [A<:IWS] {

  type ObjectList = (String, List[String], String, String, CharsetDecoder) =>
                                                                 List[Either[String,A]]
    def getLines (file:File, decoder:CharsetDecoder, FS:String): List[Either [String,A]]
    def getObjectsFromFiles( dir:File, extension:List[String],
                         decoder:CharsetDecoder, FS:String):  List[Either[String,A]] =
      getListOfFiles(dir, extension).flatMap(getLines (_, decoder, FS))

  def getObjects (path:String, extension:List[String],
            filter: String, FS:String,decoder: CharsetDecoder):List[Either[String,A]]
               = getObjectsFromFiles(new File(path),extension, decoder, FS)
  def getListOfFiles(dir: File, extensions: List[String]): List[File] =
    dir.listFiles.filter(_.isFile).toList.filter {
      file => extensions.exists(file.getName.endsWith(_))
    }

}
object ImportFunction {

  type importFuncType [A<:IWS]= (String, List[String], String, String, CharsetDecoder) => List[Either[String,A]]

   def getObjectList [A<:IWS](importFunc:importFuncType [A], path:String, extension:List[String],
                                    filter: String, FS:String, decoder: CharsetDecoder):List[A] = {
    val files = importFunc(path,extension, filter, FS, decoder)
    files.map(x =>
      x match {
        case Right(x) => x
      })
  }


  object ImportAccount extends ImportFunction [Account] {
    val pf = new PartialFunction[List[String], List[Either[String, Account]]] {
      def isDefinedAt(x: List[String]) = !x.isEmpty

      def apply(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p) =>
            try {
              val ax=Account.apply(a.trim, b.trim, c.trim, d.trim, e.trim, f.trim, g.trim, h.trim,
                i.trim, j.trim, k.trim,l.trim, m.trim, n.trim, o.trim, p.trim)
              List(Right(ax))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println("%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s %s %s".format(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p));
                List(Left(ex.getMessage))
              }
            }
          case y => {
            println("x>>>>>> %s", y)
            List(Left(y.toString))
          }
        }
    }
    override def getLines (file:File,  decoder:CharsetDecoder,
                           FS:String): List[ Either [ String,Account]]
    =scala.io.Source.fromFile(file)(decoder).getLines.toList.map(_.split(FS).toList) .flatMap(pf)
  }
  object ImportDetailsFinancialsTransaction extends ImportFunction [DetailsFinancialsTransaction] {
    val pf = new PartialFunction[List[String], List[Either[String, DetailsFinancialsTransaction]]] {
      def isDefinedAt(x: List[String]) = !x.isEmpty

      def apply(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, _, _) =>
            try {
              println("%s %s %s  %s  %s %s %s %s %s %s".format(a.trim, b.trim, c.trim, d.trim, e.trim, f.trim, g.trim, h.trim, i.trim, j.trim))
              val dx = DetailsFinancialsTransaction.apply(a.trim, b.trim, c.trim, d.trim, e.trim, f.trim, g.trim,
                h.trim, i.trim, j.trim)
              List(Right(dx))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println("%s %s %s  %s  %s %s %s %s %s %s".format(a, b, c, d, e, f, g, h, i, j));
                List(Left(ex.getMessage))
              }
            }
          case y => {
            println("x>>>>>> %s", y)
            List(Left(y.toString))
          }
        }
    }
    override def getLines (file:File,  decoder:CharsetDecoder,
                           FS:String): List[ Either [ String,DetailsFinancialsTransaction]]
     =scala.io.Source.fromFile(file)(decoder).getLines.toList.map(_.split(FS).toList) .flatMap(pf)

  }
  object ImportFinancialsTransaction extends ImportFunction [FinancialsTransaction] {
    val pf = new PartialFunction[List[String], List[Either[String, FinancialsTransaction]]] {
      def isDefinedAt(x: List[String]) = !x.isEmpty

      def apply(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t) =>
            try {
              val tx=FinancialsTransaction.apply(a.trim, b.trim, c.trim, d.trim, f.trim, h.trim, g.trim,
                p.trim, m.trim, t.trim, i.trim, o.trim, e.trim, s.trim)
              List(Right(tx))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println("%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s".format(a, b, c, d, f, h, g, p, m, t, i, o, e, s));
                List(Left(ex.getMessage))
              }
            }
          case y => {
            println("x>>>>>> %s", y)
            List(Left(y.toString))
          }
        }
    }
    override def getLines (file:File,  decoder:CharsetDecoder,
                           FS:String): List[ Either [ String,FinancialsTransaction]]
    =scala.io.Source.fromFile(file)(decoder).getLines.toList.map(_.split(FS).toList) .flatMap(pf)
  }
  object ImportSupplier extends ImportFunction [Supplier] {
    val pf = new PartialFunction[List[String], List[Either[String, Supplier]]] {
      def isDefinedAt(x: List[String]) = !x.isEmpty

      def apply(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r) =>
            try {
              val tx= Supplier.apply(a.trim, b.trim, "", c.trim, d.trim, e.trim, f.trim,  g.trim, h.trim, i.trim,
                j.trim, k.trim,  n.trim, o.trim, p.trim, q.trim, r.trim)
              List(Right(tx))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println("%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s %s %s %s %s".format(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r));
                List(Left(ex.getMessage))
              }
            }
          case y => {
            println("x>>>>>> %s", y)
            List(Left(y.toString))
          }
        }
    }
    override def getLines (file:File,  decoder:CharsetDecoder,
                           FS:String): List[ Either [ String,Supplier]]
    =scala.io.Source.fromFile(file)(decoder).getLines.toList.map(_.split(FS).toList) .flatMap(pf)
  }
  object ImportCustomer extends ImportFunction [Customer] {
    val pf = new PartialFunction[List[String], List[Either[String, Customer]]] {
      def isDefinedAt(x: List[String]) = !x.isEmpty

      def apply(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r) =>
            try {
              val tx= Customer.apply(a.trim, b.trim, "", c.trim, d.trim, e.trim, f.trim,  g.trim, h.trim, i.trim,
                j.trim, k.trim,  n.trim, o.trim, p.trim, q.trim, r.trim)
              List(Right(tx))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println("%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s %s %s %s %s".format(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r));
                List(Left(ex.getMessage))
              }
            }
          case y => {
            println("x>>>>>> %s", y)
            List(Left(y.toString))
          }
        }
    }
    override def getLines (file:File,  decoder:CharsetDecoder,
                           FS:String): List[ Either [ String,Customer]]
    =scala.io.Source.fromFile(file)(decoder).getLines.toList.map(_.split(FS).toList) .flatMap(pf)
  }
  object ImportPeriodicAccountBalance extends ImportFunction [PeriodicAccountBalance] {
    val pf = new PartialFunction[List[String], List[Either[String, PeriodicAccountBalance]]] {
      def isDefinedAt(x: List[String]) = !x.isEmpty

      def apply(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p,q) =>
            try {
              val tx= PeriodicAccountBalance.apply(a.trim, c.trim, d.trim, m.trim, n.trim, e.trim, f.trim, g.trim, h.trim)
              List(Right(tx))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println("%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s %s %s %s".format (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p,q))
                List(Left(ex.getMessage))
              }
            }
          case y => {
            println("x>>>>>> %s", y)
            List(Left(y.toString))
          }
        }
    }
    override def getLines (file:File,  decoder:CharsetDecoder,
                           FS:String): List[ Either [ String, PeriodicAccountBalance]]
    =scala.io.Source.fromFile(file)(decoder).getLines.toList.map(_.split(FS).toList) .flatMap(pf)
  }
  object ImportBankStatement extends ImportFunction [BankStatement ] {
    val pf = new PartialFunction[List[String], List[Either[String, BankStatement]]] {
      def isDefinedAt(x: List[String]) = !x.isEmpty

      def apply(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, k) =>
            try {
              val bx=BankStatement(a.trim, b.trim, c.trim, d.trim, e.trim, f.trim, g.trim, h.trim, i.trim, j.trim, k.trim)
              List(Right(bx))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println("%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s".format(a, b, c, d, f, g, h, i, j, k));
                List(Left(ex.getMessage))
              }
            }
          case y => {
            println("x>>>>>> %s", y)
            List(Left(y.toString))
          }
        }
    }
    override def getLines (file:File,  decoder:CharsetDecoder,
                           FS:String): List[ Either [ String,BankStatement ]]
    =scala.io.Source.fromFile(file)(decoder).getLines.toList.map(_.split(FS).toList) .flatMap(pf)
  }
}

  


