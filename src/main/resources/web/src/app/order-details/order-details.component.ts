import {Component, Input, OnInit} from '@angular/core';
import {OrderItem} from '../order-item';
import { flow, map, reduce } from 'lodash/fp'

@Component({
  selector: 'app-order-details',
  templateUrl: './order-details.component.html',
  styleUrls: ['./order-details.component.css']
})
export class OrderDetailsComponent implements OnInit {
  @Input() items: OrderItem[];

  constructor() { }

  ngOnInit() {
  }

  calculateTotal(): number {
    return flow([
      map((item: OrderItem) => item.getPrice() * item.getQuantity()),
      reduce((acc: number, price: number) => acc + price, 0)
    ])(this.items);
  }
}
