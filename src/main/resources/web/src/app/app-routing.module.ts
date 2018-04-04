import { NgModule } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {OrderListComponent} from './order-list/order-list.component';
import {OrderFormComponent} from './order-form/order-form.component';

const routes: Routes = [
  { path: '', redirectTo: '/orders/form', pathMatch: 'full' },
  { path: 'orders/form', component: OrderFormComponent },
  { path: 'orders', component: OrderListComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
