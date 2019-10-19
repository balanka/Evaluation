package com.iws.service

import com.iws.model._

object  Processing  {

  trait ProcessTransaction [A<:IWS] {
    def post (transaction:A):List[PeriodicAccountBalance]
    def postDebit ( periode:Int, accountId:String, amount:BigDecimal, company:String, currency:String , modelId:Int):PeriodicAccountBalance =
      postAccount(periode, accountId, amount,true, company,currency, modelId )
    def postCredit ( periode:Int, accountId:String,amount:BigDecimal , company:String, currency:String, modelId:Int )=
      postAccount(periode, accountId, amount,false, company,currency,modelId )
    private [ProcessTransaction] def  postAccount(periode:Int, accountId:String, amount:BigDecimal,isDebit:Boolean, company:String, currency:String, modelId:Int)
      = {
      val map:Map[String,PeriodicAccountBalance]=IWSCache.get(modelId).groupBy(_.id).mapValues(_.head).asInstanceOf[Map[String,PeriodicAccountBalance]] //asInstanceOf[Map[String,List[PeriodicAccountBalance]]]
      val pacc=map.getOrElse(periode.toString.concat(accountId),
        PeriodicAccountBalance(periode.toString.concat(accountId), accountId, periode, BigDecimal(0),BigDecimal(0),BigDecimal(0),  BigDecimal(0), company, currency,modelId ))
      if (isDebit) pacc.copy(debit=pacc.debit+amount) else pacc.copy(credit = pacc.credit + amount)

    }

  }
  object ProcessFinancialsTransaction extends ProcessTransaction[FinancialsTransaction] {
    override def post(transaction: FinancialsTransaction): List[PeriodicAccountBalance] =
      transaction.lines.flatMap( line =>
        List(postDebit (transaction.periode, line.account, line.amount, line.company, line.currency, transaction.modelId),
          postCredit (transaction.periode, line.oaccount, line.amount, line.company, line.currency, transaction.modelId)))
  }
}

  


