import {OrderItem} from './order-item';

export class Order {
  constructor(private id: string, private items: OrderItem[]){}
  getId() { return this.id; }
  getItems() { return this.items; }
}
