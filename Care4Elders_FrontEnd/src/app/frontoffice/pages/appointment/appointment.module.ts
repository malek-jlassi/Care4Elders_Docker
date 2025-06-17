import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AppointmentRoutingModule } from './appointment-routing.module';
import { AppointmentFormComponent } from './appointment-form/appointment-form.component';
import { AppointmentListComponent } from './appointment-list/appointment-list.component';


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    AppointmentRoutingModule,
    AppointmentFormComponent,
    AppointmentListComponent
  ]
})
export class AppointmentModule { }
