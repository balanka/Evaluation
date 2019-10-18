package com.iws.main

import java.io.File
import java.nio.charset.{CharsetDecoder, CodingErrorAction}

import com.iws.main.ImportFunction._
import com.iws.model._
import com.iws.service.Processing.ProcessFinancialsTransaction
import zio.{Ref, UIO}

import scala.io.Codec

object  Driver  {

  def main(args: Array[String]) {

    val filter = "1000"
    val FS=";"
    val extension= List ("CSV","csv","tsv")
    val pathSup= "/Users/iwsmac/Downloads/import/Supplier"
    val pathCust= "/Users/iwsmac/Downloads/import/Customer"
    val pathBS= "/Users/iwsmac/Downloads/import/bankStatement/43719244"
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
    val map=l8.groupBy(_.transId)
    val l9=l7.map(t =>t.copy( lines=map.getOrElse(t.tid,List.empty[DetailsFinancialsTransaction])))

    //val ref: UIO[Ref[Map[String, PeriodicAccountBalance]]] = Ref.make(l5.groupBy(_.id).toMap)
    IWSCache.updateAll(l1)
    IWSCache.updateAll(l2)
    IWSCache.updateAll(l3)
    IWSCache.updateAll(l4)
    IWSCache.updateAll(l5)
    IWSCache.updateAll(l6)
    FinancialsTransactionCache.updateAll(l9)
   // val r=IWSCache.get(114)
   //   l9.foreach(ProcessFinancialsTransaction.post(_:FinancialsTransaction).foreach(println))
    val r=FinancialsTransactionCache.get("1318",114)
     r.foreach(println)
      r.foreach( ProcessFinancialsTransaction.post(_:FinancialsTransaction).foreach(println))

  }

}

  


