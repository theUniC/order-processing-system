package com.theunic.orderprocessing.infrastructure.serialization

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, JsonFormat, RootJsonFormat}
import com.theunic.orderprocessing.infrastructure.remoting.DTO._

object JSON {
  object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit object UUIDFormat extends JsonFormat[UUID] {
      def write(uuid: UUID) = JsString(uuid.toString)
      def read(value: JsValue) = {
        value match {
          case JsString(uuid) => UUID.fromString(uuid)
          case _              => throw DeserializationException("Expected hexadecimal UUID string")
        }
      }
    }

    implicit val orderItemFormat: RootJsonFormat[OrderItem] = jsonFormat5(OrderItem.apply)
    implicit val orderDtoFormat: RootJsonFormat[Order] = jsonFormat2(Order.apply)
  }
}
