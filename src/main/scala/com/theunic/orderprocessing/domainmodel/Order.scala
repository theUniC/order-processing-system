package com.theunic.orderprocessing.domainmodel

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern._
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

object Order {
  import com.theunic.orderprocessing.infrastructure.remoting.DTO

  sealed trait OrderMessage {
    def orderId: UUID
  }

  // Messages
  case class CreateNewOrder(orderId: UUID, items: Iterable[DTO.OrderItem]) extends OrderMessage
  case class AddOrderLine(orderId: UUID, name: String, price: BigDecimal, quantity: Int) extends OrderMessage
  case class OrderTotal(orderId: UUID) extends OrderMessage

  // Responses
  case class CalculatedOrderTotal(orderId: UUID, total: BigDecimal) extends OrderMessage

  def props(id: UUID): Props = Props(new Order(id))
}

final class Order(id: UUID) extends Actor with ActorLogging {
  import Order._
  import OrderItem._
  import context.dispatcher

  implicit val timeout = Timeout(500 millis)
  private var orderItems: Seq[ActorRef] = scala.collection.Seq()

  override def receive: Receive = {
    case AddOrderLine(_, name, price, quantity) =>
      val item = context.actorOf(OrderItem.props(name, price, quantity), s"item-${UUID.randomUUID()}")
      orderItems = orderItems :+ item
      context watch item

    case OrderTotal(orderId) =>
      Future
        .sequence(orderItems.map(_.ask(CalculateTotal).mapTo[BigDecimal]))
        .map(_.sum)
        .map(CalculatedOrderTotal(orderId, _))
        .map(sender() !)
  }
}

object Orders {
  def props: Props = Props[Orders]
}

final class Orders extends Actor with ActorLogging {
  import Order._

  var orders: Map[UUID, ActorRef] = Map()

  override def receive: Receive = {
    case CreateNewOrder(orderId, items) =>
      val newOrder = context.actorOf(Order.props(orderId), orderId.toString)
      orders = orders + (orderId -> newOrder)
      context watch newOrder
      items
        .map(o => AddOrderLine(orderId, o.name, o.price, o.quantity))
        .foreach(newOrder !)

    // Forward the rest of the messages
    case m: OrderMessage => orders(m.orderId) forward m
  }
}

object OrderItem {
  def props(name: String, price: BigDecimal, quantity: Int): Props = Props(new OrderItem(name, price, quantity))

  // Messages
  case object CalculateTotal

  // Responses
}

final class OrderItem(name: String, price: BigDecimal, quantity: Int) extends Actor with ActorLogging {
  import OrderItem._

  override def receive: Receive = {
    case CalculateTotal => sender() ! (price * quantity)
  }
}
