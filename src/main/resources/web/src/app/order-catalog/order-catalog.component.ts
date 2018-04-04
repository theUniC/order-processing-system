import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AvailableItem} from '../available-item';

@Component({
  selector: 'app-order-catalog',
  templateUrl: './order-catalog.component.html',
  styleUrls: ['./order-catalog.component.css']
})
export class OrderCatalogComponent implements OnInit {
  @Input() availableItems: AvailableItem[];
  @Output() availableItemClick: EventEmitter<AvailableItem> = new EventEmitter<AvailableItem>();

  constructor() { }

  ngOnInit() {
  }

  clickAvailableItem(item: AvailableItem) {
    this.availableItemClick.emit(item);
  }
}
