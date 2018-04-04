import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';


import { AppComponent } from './app.component';
import { OrderFormComponent } from './order-form/order-form.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { OrderDetailsComponent } from './order-details/order-details.component';
import { OrderCatalogComponent } from './order-catalog/order-catalog.component';
import {HttpClientModule} from '@angular/common/http';
import {OrdersService} from './orders.service';
import { AppRoutingModule } from './app-routing.module';
import { OrderListComponent } from './order-list/order-list.component';

@NgModule({
  declarations: [
    AppComponent,
    OrderFormComponent,
    OrderDetailsComponent,
    OrderCatalogComponent,
    OrderListComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    NgbModule.forRoot(),
    AppRoutingModule
  ],
  providers: [OrdersService],
  bootstrap: [AppComponent]
})
export class AppModule { }
