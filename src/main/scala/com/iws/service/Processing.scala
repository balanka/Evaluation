package com.iws.service

import com.iws.model._

  trait ProcessTransaction [A<:IWS] {

    private [this] def getMapFor(modelId:Int):Map[String,PeriodicAccountBalance]=IWSCache.get(modelId).groupBy(_.id).mapValues(_.head)
      .asInstanceOf[Map[String,PeriodicAccountBalance]]

    def post (transaction:A):List[PeriodicAccountBalance]
    protected [this] def postDebit ( periode:Int, accountId:String, amount:BigDecimal,
                                         company:String, currency:String , modelId:Int) =
      postAccount(periode, accountId, amount,true, company,currency, modelId )
    protected [this]  def postCredit ( periode:Int, accountId:String,amount:BigDecimal,
                                           company:String, currency:String, modelId:Int )=
      postAccount(periode, accountId, amount,false, company,currency,modelId )
    private [this] def  postAccount(periode:Int, accountId:String, amount:BigDecimal,isDebit:Boolean,
                                    company:String, currency:String, modelId:Int)
      = {
      val pacc=getMapFor(modelId).getOrElse(periode.toString.concat(accountId),
        PeriodicAccountBalance(periode.toString.concat(accountId), accountId, periode, BigDecimal(0),BigDecimal(0),
          BigDecimal(0),  BigDecimal(0), company, currency,modelId ))
      if (isDebit) {
        updateFollowingBalances(periode,accountId, pacc.debit+amount, modelId, isDebit)
        pacc.copy(debit=pacc.debit+amount)

      } else {
        updateFollowingBalances(periode,accountId, pacc.credit+amount, modelId, isDebit)
        pacc.copy(credit = pacc.credit + amount)
      }

    }
    def updatePacc (pacc:PeriodicAccountBalance, amount:BigDecimal, isDebit:Boolean) =
      if(isDebit)
      pacc.copy(idebit = pacc.idebit+amount)
    else
        pacc.copy(icredit = pacc.credit+amount)

    def updateFollowingBalances(currentPeriode:Int,accountId:String, amount:BigDecimal, modelId:Int, isDebit:Boolean): Unit ={
     val l =getMapFor(modelId)
       for( i<- currentPeriode to 12-currentPeriode)
       l.get(i.toString.concat(accountId)).toList.map(updatePacc(_, amount,isDebit ))
       //val map:Map[String,PeriodicAccountBalance]=IWSCache.get(modelId).groupBy(_.id).mapValues(_.head)
       // .asInstanceOf[Map[String,PeriodicAccountBalance]]
    }

  }
  object ProcessFinancialsTransaction extends ProcessTransaction[FinancialsTransaction] {
    override def post(transaction: FinancialsTransaction): List[PeriodicAccountBalance] =
      transaction.lines.flatMap( line =>
        List(postDebit (transaction.periode, line.account, line.amount, line.company, line.currency, transaction.modelId),
          postCredit (transaction.periode, line.oaccount, line.amount, line.company, line.currency, transaction.modelId)))
  }


  


