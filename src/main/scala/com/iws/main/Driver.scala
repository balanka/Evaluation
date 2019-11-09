package com.iws.main

import java.io.File
import java.nio.charset.{CharsetDecoder, CodingErrorAction}
import java.time.LocalDate
import cats.kernel.Monoid
import cats.implicits._
import com.iws.main.ImportFunction._
import com.iws.model._
import com.iws.service.ProcessFinancialsTransaction
import zio.{Ref, UIO}

import scala.io.Codec

object  Driver  {

  def main(args: Array[String]) {
    val d1= LocalDate.of(2016, 1, 4)
    val d2= LocalDate.of(2016, 1, 11)
    val d3= LocalDate.of(2016, 1, 18)
    val d4= LocalDate.of(2016, 1, 25)
    val p1= PeriodicAccountBalance("4711", "201801", "0", "0", "0", "0","1000", "EUR" )
    val p2= PeriodicAccountBalance("4711", "201802", "0", "0", "0", "0","1000", "EUR" )
    val p3= PeriodicAccountBalance("4711", "201803", "0", "0", "0", "0","1000", "EUR" )
    val p4= PeriodicAccountBalance("4711", "201804", "0", "0", "0", "0","1000", "EUR" )
 val pCounts:List[Map[PeriodicAccountBalance,BigDecimal]] =
      List(
        Map(
          p1 -> BigDecimal(37),
          p2  -> BigDecimal(64),
          p3  ->BigDecimal(54)
        ),
        Map(
          p3 -> BigDecimal(60),
          p3-> BigDecimal(2),
          p4 -> BigDecimal(75)
        ),
        Map(
          p1 -> BigDecimal(30)
        )
      )
   /* val birdCounts=
      List(
        Map(
          (d1, "cologne", "pigeon") -> BigDecimal(37),
          (d2, "bonn", "starling") -> BigDecimal(64),
          (d3, "cologne", "bullfinch") ->BigDecimal(54)
        ),
        Map(
          (d3, "cologne", "bullfinch") -> BigDecimal(60),
          (d3, "bonn", "bullfinch") -> BigDecimal(2),
          (d4, "dusseldorf", "bullfinch") -> BigDecimal(75)
        ),
        Map(
          (d1, "cologne", "pigeon") -> BigDecimal(30)
        )
      )
       val x:Map[(LocalDate, String, String), BigDecimal]= birdCounts
      .flatMap(_.toList)
      .groupBy(_._1)
      .map({ case (k,v) => (k, v.map(_._2).sum) })
    println("x>>>>> "+x +"\n"+ birdCounts.combineAll)
    */
   val x:Map[PeriodicAccountBalance, BigDecimal]= pCounts
      .flatMap(_.toList)
      .groupBy(_._1)
      .map({ case (k,v) => (k, v.map(_._2).sum) })
    println("pCounts >>>>> "+x +"\n pCounts2: "+ pCounts.combineAll)
    System.exit(0)

    val filter = "1000"
    val FS=";"
    val extension= List ("CSV","csv","tsv")
    val pathSup= "/Users/iwsmac/Downloads/import/Supplier"
    val pathCust= "/Users/iwsmac/Downloads/import/Customer"
    //val pathBS= "/Users/iwsmac/Downloads/import/bankStatement/43719244"
    val pathBS= "/Users/iwsmac/Downloads/import/bankStatement/43006329"
    val pathPAB="/Users/iwsmac/Downloads/import/periodicAccountBalance"
    val pathAcc= "/Users/iwsmac/Downloads/import/account"
    val pathBacc= "/Users/iwsmac/Downloads/import/bankAccount"
    val path1= "/Users/iwsmac/Downloads/import/masterFinancialsTransaction"
    val path2= "/Users/iwsmac/Downloads/import/detailsFinancialsTransaction"
    val decoder = Codec.UTF8.decoder.onMalformedInput(CodingErrorAction.IGNORE)


    val l1= getObjectList(ImportSupplier.getObjects, pathSup,extension, filter, FS, decoder)
    val l2= getObjectList(ImportCustomer.getObjects, pathCust,extension, filter, FS, decoder)
    val l3= getObjectList(ImportAccount.getObjects, pathAcc,extension, filter, FS, decoder)
    val l4= getObjectList(ImportBankAccount.getObjects, pathBacc, extension, filter, FS, decoder)
    val l5= getObjectList(ImportPeriodicAccountBalance.getObjects, pathPAB,extension, filter, FS, decoder)
    val l6= getObjectList(ImportBankStatement.getObjects, pathBS,extension, filter, FS, decoder)
    val l7:List[FinancialsTransaction]= getObjectList(ImportFinancialsTransaction.getObjects, path1,extension, filter, FS, decoder)
    val l8:List[DetailsFinancialsTransaction] = getObjectList(ImportDetailsFinancialsTransaction.getObjects, path2,extension, filter, FS, decoder)
   // val s=l6.fold(BigDecimal(0.0))((x:BankStatement, y:BankStatement)=> Monoid [BankStatement].combine(x,y))
    val map=l8.groupBy(_.transId)
    val l9=l7.map(t =>t.copy( lines=map.getOrElse(t.tid,List.empty[DetailsFinancialsTransaction])))

    //val ref: UIO[Ref[Map[String, PeriodicAccountBalance]]] = Ref.make(l5.groupBy(_.id).toMap)
    MasterfileCache.updateAll(l1)
    MasterfileCache.updateAll(l2)
    MasterfileCache.updateAll(l3)
    IWSCache.updateAll(l4)
    IWSCache.updateAll(l5)
    IWSCache.updateAll(l6)
    FinancialsTransactionCache.updateAll(l9)
    val periodicAccountBalances =IWSCache.get(106).asInstanceOf[List[PeriodicAccountBalance]].filter(_.company.equals("1000"))
    val accounts:List[Account]=MasterfileCache.get(9).asInstanceOf[List[Account]].filter(_.company.equals("1000"))
    //Account.addAllSubAccounts(accounts).filter(_.parentId.equals("6")).filter(_.id.equals("7300")).foreach(println)

    Account.addAllSubAccounts(accounts).filter(_.parentId.equals("6")).filter(_.id.equals("7300")).foreach(println)
    periodicAccountBalances.foreach(println)
    val abc=Account.addBalances(201802,accounts, periodicAccountBalances ).flatMap(_.balances)
      abc.foreach(println)
    //Account.addBalances(201801,accounts, periodicAccountBalances ).flatMap(_.balances).foreach(println) //.filter(_.parentId.equals("6")).filter(_.id.equals("7300")).foreach(println)

    //accounts.filter(_.parentId.equals("6")).foreach(println)
    //val xx=accounts.map(x=> x.copy( subAccounts=accounts.filter(_.parentId.equals(x.id))))
    //xx.filter(_.parentId.equals("6")).foreach(println)

    //def getParent(parentId:String, accounts:List[Account]): Option[Account] = accounts.filter(_.id.equals(parentId)).headOption
    //........Account.addAllSubAccounts(accounts).filter(_.id.equals("9801")).filter(_.company.equals("1000")).foreach(println)
   // val r=IWSCache.get(114)
   //   l9.foreach(ProcessFinancialsTransaction.post(_:FinancialsTransaction).foreach(println))
    //val r=FinancialsTransactionCache.get("1318",114)
   // r.foreach( ProcessFinancialsTransaction.post(_:FinancialsTransaction).foreach(println))
    //val accounts:List[Account]=MasterfileCache.get(9).asInstanceOf[List[Account]]
    //accounts.map(m =>accounts.filter(_.id.equals(m.parentId)).headOption.map(_.addMe(m))).flatten.filter(_.id.equals("9900")).foreach(println)
   //<<<<<accounts.map( x => accounts.filter(_.id.equals(x.parentId)).headOption.map(_.addMe(x))).flatten.filter(_.id.equals("0095")).filter(_.company.equals("1000")).foreach(println)

    //accounts.map( x => getParent (x.parentId, accounts).headOption.map(_.addMe(x))).flatten.filter(_.id.equals("9902")).foreach(println)
    //>>>accounts.map( x => getParent (x.parentId, accounts).headOption.map(_.addMe(x))).flatten.filter(_.id.equals("0095")).filter(_.company.equals("1000")).foreach(println)
   // accounts.map(m =>m.addMe(accounts.filter(_.parentId.equals(m.id)).headOption.map(_.addMe(m))).flatten.filter(_.id.equals("9900")).foreach(println)
    //val accountsx:List[Account]=MasterfileCache.get(9).asInstanceOf[List[Account]]
    //val ac=accounts.map(x =>x.copy(subAccounts = accounts.filter(_.parentId.equals( x.id) ))).filter(_.id.equals("9900")).foreach(println)

   // val ac=accounts.map(x =>x.add(accounts.filter(_.parentId.equals( x.id) ))).filter(_.id.equals("9900")).foreach(println)
   //val r=MasterfileCache.get("9900",9).asInstanceOf[Option[Account]].map(x =>{


 //Account.buildSubAccounts(r).foreach(println)



  }

}

  


