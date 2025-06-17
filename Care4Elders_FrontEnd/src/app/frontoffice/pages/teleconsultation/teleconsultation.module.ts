import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TeleconsultationRoutingModule } from './teleconsultation-routing.module';
import { TeleconsultationListComponent } from './teleconsultation-list/teleconsultation-list.component';
import { TeleconsultationFormComponent } from './teleconsultation-form/teleconsultation-form.component';
import { MatPaginatorModule } from '@angular/material/paginator';
import { TeleconsultationSummaryDialogComponent } from './teleconsulation-summary-dialog/teleconsulation-summary-dialog.component';

@NgModule({
  imports: [
    CommonModule,
    TeleconsultationRoutingModule,
    TeleconsultationListComponent,
    TeleconsultationFormComponent,
    TeleconsultationSummaryDialogComponent,
    MatPaginatorModule
  ]
})
export class TeleconsultationModule { }
