package com.theunic.orderprocessing.infrastructure.remoting

import java.util.UUID

object DTO {
  case class OrderItem(id: UUID, name: String, description: String, price: BigDecimal, quantity: Int)
  case class Order(id: UUID, items: Seq[OrderItem] = Seq())
}
