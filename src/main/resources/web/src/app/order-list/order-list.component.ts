import {Component, NgZone, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Order} from '../order';

@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.css']
})
export class OrderListComponent implements OnInit {
  private orders$: Observable<Order>;
  constructor(private zone: NgZone) { }
  ngOnInit() {
    Observable.create((observer) => {
      const eventSource = new window['EventSource']('/api/events');
      eventSource.onmessage = (event) => {
        if (event.data) {
          this.zone.run(() => observer.next(JSON.parse(event.data)));
        }
      };
      eventSource.onerror = error => this.zone.run(() => observer.error(error));
      return () => eventSource.close();
    }).subscribe(result => this.orders$ = result);
  }
}
