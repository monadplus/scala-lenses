package io.monadplus.scalalens

/**
  * Source: https://github.com/circe/circe
  */
import cats.implicits._
import io.circe._, io.circe.parser._
import io.circe.optics.JsonPath._

object CirceJsonPathExample extends App {
  val json: Json = parse("""
{
  "order": {
    "customer": {
      "name": "Custy McCustomer",
      "contactDetails": {
        "address": "1 Fake Street, London, England",
        "phone": "0123-456-789"
      }
    },
    "items": [{
      "id": 123,
      "description": "banana",
      "quantity": 1
    }, {
      "id": 456,
      "description": "apple",
      "quantity": 2
    }],
    "total": 123.45
  }
}
""").getOrElse(Json.Null)

// Regular way
  json.hcursor
    .downField("order")
    .downField("customer")
    .downField("contactDetails")
    .get[String]("phone")
    .toOption
// phoneNum: Option[String] = Some(0123-456-789)


// Using optics (and Dynamics)
  val _phoneNum = root.order.customer.contactDetails.phone.string
  _phoneNum.getOption(json)
// phoneNum: Option[String] = Some(0123-456-789)

  val doubleQuantities: Json => Json =
    root.order.items.each.quantity.int.modify(_ * 2)

  doubleQuantities(json)
}
