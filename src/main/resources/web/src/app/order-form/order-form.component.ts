import { Component, OnInit } from '@angular/core';
import { OrderItem } from '../order-item';
import {AvailableItem} from '../available-item';
import { v4 } from 'uuid';
import { flow, filter, invokeMap } from "lodash/fp";
import {OrdersService} from '../orders.service';
import {Order} from '../order';

@Component({
  selector: 'app-order-form',
  templateUrl: './order-form.component.html',
  styleUrls: ['./order-form.component.css']
})
export class OrderFormComponent implements OnInit {
  private id: string = v4();
  private orderItems: OrderItem[] = [];
  private creatingNewOrder: boolean = false;
  private showAlert: boolean = false;
  private newOrderCreatedSuccessfully: boolean = false;
  private availableItems: AvailableItem[] = [
    new AvailableItem(v4(), "Patatas Bravas", "assets/images/bravas.jpeg", 5),
    new AvailableItem(v4(), "Pulpo a la gallega", "assets/images/pulpo.jpg", 15),
    new AvailableItem(v4(),"Croquetas caseras", "assets/images/croquetas.jpg", 10),
  ];

  constructor(private ordersService: OrdersService) { }

  ngOnInit() {
  }

  addItemToOrder(availableItem: AvailableItem): void {
    const orderItemExists = this.orderItems.filter(orderItem => availableItem.getId() === orderItem.getId()).length > 0;

    if (!orderItemExists) {
      this.orderItems.push(new OrderItem(
        availableItem.getId(),
        availableItem.getName(),
        availableItem.getName(),
        availableItem.getPrice()
      ));
    }

    flow([
      filter((orderItem: OrderItem) => availableItem.getId() === orderItem.getId()),
      invokeMap('increaseQuantity')
    ])(this.orderItems);
  }

  createNewOrder() {
    this.creatingNewOrder = !this.creatingNewOrder;
    this.ordersService.addOrder(new Order(this.id, this.orderItems)).subscribe(order => {
      this.creatingNewOrder = !this.creatingNewOrder;
      this.showAlert = !this.showAlert;
      this.newOrderCreatedSuccessfully = !this.newOrderCreatedSuccessfully;
      this.id = v4();
      this.orderItems = [];
    });
  }
}
