package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import views._
import models._
import scala.collection.mutable.ArrayBuffer
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.data.format.Formats._
import java.util.Calendar

object Transactions extends Controller {
  
  val transactionForm: Form[Transaction] = Form(
    
      mapping(
          "symbol" -> nonEmptyText,
          "ttype" -> nonEmptyText,
          "price" -> bigDecimal, 
          "quantity" -> number, 
          "notes"  -> text
      )
      // transactionForm -> Transaction
      ((symbol, ttype, price, quantity, notes) => Transaction(0, symbol, ttype, price, quantity, Calendar.getInstance.getTime, notes))
      
      ((t: Transaction) => Some(t.symbol, t.ttype, t.price.toDouble, t.quantity, t.notes))
  )
  
  def add = Action{ implicit request => 
    transactionForm.bindFromRequest.fold(
        errors => {
          Ok(Json.toJson(Map("success"-> toJson(false), "msg"->toJson("Boom!"), "id"->toJson(0))))
        }, 
        transaction => {
          val id = Transaction.insert(transaction)
          id match{
            case Some(autoIncrementId) =>
              val result = Map("success"-> toJson(true), "msg"-> toJson("Success!"), "id"-> toJson(autoIncrementId))
              Ok(Json.toJson(result))
            case None =>
              val result = Map("success"-> toJson(true), "msg"-> toJson("Success!"), "id"-> toJson(-1))
              Ok(Json.toJson(result))
          }
        }
    )  
  }
  
}
