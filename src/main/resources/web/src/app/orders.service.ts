import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Order} from './order';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class OrdersService {
  constructor(private $http: HttpClient) { }
  addOrder(order: Order): Observable<Order> {
    return this.$http.put<Order>(`http://localhost:3000/api/orders/${order.getId()}`, order)
  }
}
