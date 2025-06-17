import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TeleconsultationFormComponent } from './teleconsultation-form/teleconsultation-form.component';
import { TeleconsultationListComponent } from './teleconsultation-list/teleconsultation-list.component';


const routes: Routes = [
  { path: '', component: TeleconsultationListComponent },
  { path: 'add', component: TeleconsultationFormComponent },
  { path: 'edit/:id', component: TeleconsultationFormComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TeleconsultationRoutingModule {}
