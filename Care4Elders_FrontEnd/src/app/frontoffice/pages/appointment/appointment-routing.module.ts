import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppointmentFormComponent } from './appointment-form/appointment-form.component';
import { AppointmentListComponent } from './appointment-list/appointment-list.component';

const routes: Routes = [
  // Default route for appointments - redirects to list
  {
    path: '',
    redirectTo: 'list',
    pathMatch: 'full'
  },
  {
    path: 'add',
    component: AppointmentFormComponent
  },
  {
    path: 'edit/:id',
    component: AppointmentFormComponent
  },
  {
    path: 'list',
    component: AppointmentListComponent
  },
  // Catch any other paths under appointments and redirect to list
  {
    path: '**',
    redirectTo: 'list'
  }
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AppointmentRoutingModule { }
