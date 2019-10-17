package com.iws.model

import java.text.SimpleDateFormat

import spray.json.DefaultJsonProtocol._
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

import com.iws.model.common.Amount
import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}

import scala.collection.immutable.TreeMap
final case class Speech(
  speaker:String,
  theme:String,
  day:String,
  words:Int 
)
object common{
  type Amount = scala.math.BigDecimal
}

trait ContainerT [+A<:IWS,-B<:IWS] {
  def update(newItem: B): ContainerT [A,B]
  def updateAll(all: Seq[B]): ContainerT [A,B]
  def remove (item: B): ContainerT  [A,B]
  def size = items.size
  def items : Seq[A]
  def add(newItem: B): ContainerT [A,B]
}
final case class MasterfileId(value: String) extends AnyVal
sealed trait IWS {
  def id: String //MasterfileId
  def modelId: Int
}
case class Data  (items: Seq[IWS]) extends ContainerT [IWS,IWS]{
  override def update(newItem: IWS) = {
    items.indexWhere((_.id == newItem.id)) match {
      case -1 =>
        Data(items :+ newItem)
      case index =>
        Data(items.updated(index, newItem))
    }
  }
  override def updateAll(all: Seq[IWS]) =  Data((items.toSet ++all.toSet).toList)
  override def add(newItem: IWS)= Data(items :+ newItem)
  override def remove (item: IWS) = Data(items.filterNot(_.id==item.id))
}

object Currency extends Enumeration {
  type ccy = Value
  val CHF, CNY,  EUR, DEM, GNF, JPY, USD, XOF = Value
}
object Speech { 
 implicit object DateJsonFormat extends RootJsonFormat[LocalDate] {
    
     val formatter:DateTimeFormatter= DateTimeFormatter.ISO_LOCAL_DATE

    override def write(obj: LocalDate) = JsString(obj.format(formatter))

    override def read(json: JsValue) : LocalDate = json match {
      case JsString(s) => LocalDate.parse(s, formatter) 
      case _ => throw DeserializationException("Invalid date format: " + json)
    }
  }  
implicit val speechJsonFormat: RootJsonFormat[Speech] = jsonFormat4(Speech.apply)
}
final case  class Account (id:String, name:String, description:String, dateofopen:Date, dateofclose:Date,
                           balance:BigDecimal, company:Int, parentId:String, isDebit:Boolean, isBalanceSheetAccount:Boolean,
                           posted:Date, updated:Date, typeJournal:Int, modelId:Int, isResultAccount:Boolean,
                           isIncomeStatementAccount:Boolean) extends IWS


object Account {

  val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
  val dateFormat = new SimpleDateFormat(DATE_FORMAT)
  implicit def int2Boolean(i:Int) = i==1
 def apply (id:String, name:String, description:String, dateofopen:String, dateofclose:String,
    balance:String, company:String, parentId:String, isDebit:String, isBalanceSheetAccount:String,
    posted:String, updated:String, typeJournal:String, modelId:String, isResultAccount:String,
    isIncomeStatementAccount:String) =
    new Account (id, name, description, dateFormat.parse(dateofopen), dateFormat.parse(dateofclose),
      BigDecimal(balance),company.toInt, parentId, isDebit.toInt, isBalanceSheetAccount.toInt,
      dateFormat.parse(posted), dateFormat.parse(updated),0,modelId.toInt,
      isResultAccount.toInt, isIncomeStatementAccount.toInt )
}
final case  class PeriodicAccountBalance ( id:String, accountId:String, periode:Int, idebit:BigDecimal, icredit:BigDecimal,
                                            debit:BigDecimal, credit:BigDecimal,
                                            company:Int, currency:String, modelId:Int = 106) extends IWS
object PeriodicAccountBalance {
 def apply ( id:String, accountId:String, periode:String, idebit:String, icredit:String,
             debit:String, credit:String, company:String, currency:String) =
   new PeriodicAccountBalance(id, accountId, periode.toInt, BigDecimal(idebit), BigDecimal(icredit),
     BigDecimal(debit), BigDecimal(credit), company.toInt, currency )
}
final case  class FinancialsTransaction (  tid:Long, oid:Long, costCenter:String, account:String,
                                          transdate:Date, enterdate:Date, postingdate:Date, periode:Int,posted:Boolean,
                                          modelId:Int, company:String, currency:String, text:String, typeJournal:Int,
                                        lines:List[DetailsFinancialsTransaction]) extends IWS {
  def id = tid.toString
}
object FinancialsTransaction {
  val DATE_FORMAT = "yyyy-MM-dd"
  val dateFormat = new SimpleDateFormat(DATE_FORMAT)
  implicit def int2Boolean(i:Int) = i==1
  def typeJournal2Int (typeJournal:String) = if(typeJournal.isEmpty) 0 else typeJournal.toInt
  def apply ( id:String, oid:String, costCenter:String, account:String,
              transdate:String, enterdate:String, postingdate:String, periode:String, posted:String,
              modelId:String, company:String, currency:String, text:String, typeJournal:String) =
    new FinancialsTransaction( id.toLong, oid.toLong, costCenter, account,
      dateFormat.parse(transdate), dateFormat.parse(enterdate),dateFormat.parse(postingdate),
      periode.toInt, posted.toInt, modelId.toInt, company,currency, text, typeJournal2Int(typeJournal),
      List.empty[DetailsFinancialsTransaction])
}
final case  class DetailsFinancialsTransaction ( lid:Long, transId:Long, account:String, side:Boolean,oaccount:String,
                    amount:BigDecimal, duedate:Date, text:String, currency:String, modelId:Int, company:String) extends IWS {
  def id=lid.toString
  def name=text
}
object DetailsFinancialsTransaction {
  val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
  val dateFormat = new SimpleDateFormat(DATE_FORMAT)
  implicit def int2Boolean(i:Int) = i==1
  def apply ( id:String, transId:String, account:String, side:String,oaccount:String,
              amount:String, duedate:String, text:String, currency:String, modelId:String)=
    new DetailsFinancialsTransaction( id.toLong, transId.toLong, account, side.toInt, oaccount,
      BigDecimal(amount.replace (".0000", ".00")),
      dateFormat.parse(duedate),text, currency, modelId.toInt, "1000" )
}
final case  class Customer (id:String, name:String, description:String, street:String, city:String, state:String,
                            zipCode:String, tel:String, email:String, accountId:String, companyId:String, iban:String,
                           vatCode:String, oAccountId:String, postingdate:Date,updated:Date, modelId:Int) extends IWS
object Customer {
  val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
  val dateFormat = new SimpleDateFormat(DATE_FORMAT)
  def apply (id:String, name:String, description:String, street:String, city:String, state:String,
             zipCode:String, tel:String, email:String, accountId:String, companyId:String, iban:String,
             vatCode:String, oAccountId:String, postingdate:String,updated:String, modelId:String) =
    new Customer (id, name, description, street, city, state,zipCode,tel, email, accountId, companyId, iban,
      vatCode, oAccountId, dateFormat.parse(postingdate), dateFormat.parse(updated), modelId.toInt )
}

final case  class Supplier (id:String, name:String, description:String, street:String, city:String, state:String,
                            zipCode:String, tel:String, email:String, accountId:String, companyId:String, iban:String,
                            vatCode:String, oAccountId:String, postingdate:Date,updated:Date, modelId:Int) extends IWS
object Supplier {
  val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
  val dateFormat = new SimpleDateFormat(DATE_FORMAT)
  def apply (id:String, name:String, description:String, street:String, city:String, state:String,
             zipCode:String, tel:String, email:String, accountId:String, companyId:String, iban:String,
             vatCode:String, oAccountId:String, postingdate:String,updated:String, modelId:String) =
    new Supplier (id, name, description, street, city, state,zipCode,tel, email, accountId, companyId, iban,
      vatCode, oAccountId, dateFormat.parse(postingdate), dateFormat.parse(updated), modelId.toInt )
}
final case  class BankStatement (auftragskonto:String,
                                 buchungstag:String,
                                 valutadatum:String,
                                 buchungstext:String,
                                 verwendungszweck:String,
                                 beguenstigter:String,
                                 kontonummer:String,
                                 blz:String,
                                 betrag:String,
                                 waehrung:String,
                                 info:String, modelId:Int=999) extends IWS
{
  def id = auftragskonto
  def name= buchungstext
}
object BankStatement {

  implicit object DateJsonFormat extends RootJsonFormat[LocalDate] {

    val formatter:DateTimeFormatter= DateTimeFormatter.ISO_LOCAL_DATE

    override def write(obj: LocalDate) = JsString(obj.format(formatter))

    override def read(json: JsValue) : LocalDate = json match {
      case JsString(s) => LocalDate.parse(s, formatter)
      case _ => throw DeserializationException("Invalid date format: " + json)
    }
  }
  implicit val bankStatementJsonFormat: RootJsonFormat[BankStatement] = jsonFormat12(BankStatement.apply)
}

object IWSCache { //extends Subject [IWS, IWS]  with  Observer [IWS]{

  private var cache = new TreeMap[Int, Data]

  /*
    private var observers: List[Observer[IWS]] = Nil
   override def addObserver(observer: Observer[IWS]) = observers = observer :: observers

   override def notifyObservers() = observers.foreach(_.update(this))

   */

  def +(item: IWS): Option[IWS] = update(item)

  def update(item: IWS): Option[IWS] = {

    if( !cache.contains(item.modelId)) {
      cache +=(item.modelId ->Data(List(item)))
    } else {
      cache.getOrElse(item.modelId, Data(List(item))).update(item)
    }
    Some(item)

  }

  def updateAll(l: List[IWS]): Option[Data ]= {

    if(l==null || l.isEmpty)  return  None
    else {
      val l2:Data = cache.getOrElse(l.head.modelId, Data(l))
      cache +=(l.head.modelId -> l2.updateAll(l))
      Some(l2)
    }
  }

  def get(item: IWS): Seq[IWS] = cache.getOrElse(item.modelId, Data(List.empty[IWS])).items
  def get(modelId:Int): Seq[IWS] = {val x =cache.getOrElse(modelId, Data(List.empty[IWS])); println(x); println(x.items); x.items}
  def delete(item: IWS): Data = cache.getOrElse(item.modelId, Data(List.empty[IWS])).remove(item)

  def list(item:IWS, pageSize: Int, offset: Int): List[IWS] =
    cache.getOrElse(item.modelId, Data(List.empty[IWS]))
      //.items.toList.sortBy(_.id.value).slice(offset, offset + pageSize)
      .items.toList.sortBy(_.id).slice(offset, offset + pageSize)

  def all(modelId:Int, pageSize: Int, offset: Int): List[IWS] =
    cache.getOrElse(modelId, Data(List.empty[IWS]))
      .items.toList.sortBy(_.id).slice(offset, offset + pageSize)
}