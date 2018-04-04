import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderCatalogComponent } from './order-catalog.component';

describe('OrderCatalogComponent', () => {
  let component: OrderCatalogComponent;
  let fixture: ComponentFixture<OrderCatalogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OrderCatalogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OrderCatalogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
