package com.theunic.orderpressing.domainmodel

import java.util.UUID

import akka.actor.{ActorIdentity, ActorSystem, Identify}
import akka.pattern._
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import com.theunic.orderprocessing.domainmodel.Order.{CalculatedOrderTotal, CreateNewOrder, OrderTotal}
import com.theunic.orderprocessing.domainmodel.Orders
import com.theunic.orderprocessing.infrastructure.remoting.DTO
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._

class OrderSpec
  extends TestKit(ActorSystem("tests"))
    with ImplicitSender
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  implicit val timeOut: Timeout = Timeout(5 seconds)

  private val orders = system.actorOf(Orders.props, "orders")

  "A Orders actor" must {
    "be able to create new empty order" in {
      val id = UUID.randomUUID()
      orders ! CreateNewOrder(id, Seq())
      val asker = new AskableActorSelection(system.actorSelection(s"akka://tests/user/orders/$id"))
      val future = asker.ask(Identify(1))
      val identity = Await.result(future, 5 seconds).asInstanceOf[ActorIdentity]
      awaitCond(identity.getActorRef != null, 500 millis)
    }

    "be able to create orders with several order items" in {
      val id = UUID.randomUUID()
      orders ! CreateNewOrder(id, Seq(DTO.OrderItem(UUID.randomUUID(), "test", "test", 10, 10)))
      val asker = new AskableActorSelection(system.actorSelection(s"akka://tests/user/orders/$id"))
      val future = asker.ask(Identify(1))
      val identity = Await.result(future, 5 seconds).asInstanceOf[ActorIdentity]
      awaitCond(identity.getActorRef != null, 500 millis)
    }

    "be able to calculate order total given an exisiting order in the actor system" in {
      val id = UUID.randomUUID()
      orders ! CreateNewOrder(
        id,
        Seq(
          DTO.OrderItem(UUID.randomUUID(), "test1", "test1", 10, 2),
          DTO.OrderItem(UUID.randomUUID(), "test2", "test2", 5, 3)
        )
      )

      val future = orders ? OrderTotal(id)
      val total = Await.result(future, 5 seconds).asInstanceOf[CalculatedOrderTotal]

      total.total shouldBe ((10 * 2) + (5 * 3))
    }
  }
}
