import { Component, OnInit } from '@angular/core';

import { Router } from '@angular/router';

import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { firstValueFrom } from 'rxjs';
import { UtilisateurService } from '../../../../services/utilisateur.service';
import { Utilisateur } from '../../../../models/Utilisateur.model';
import { Teleconsultation } from '../../../../models/teleconsultation.model';
import { TeleconsultationService } from '../../../../services/teleconsultation.service';
import { TopbarComponent } from '../../../../shared/layout/topbar/topbar.component';
import { NavbarComponent } from '../../../../shared/layout/navbar/navbar.component';
import { FooterComponent } from '../../../../shared/layout/footer/footer.component';

@Component({
  selector: 'app-teleconsultation-list',
  templateUrl: './teleconsultation-list.component.html',
  styleUrls: ['./teleconsultation-list.component.css'],
  imports: [
    CommonModule,
    RouterModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    TopbarComponent,
    NavbarComponent,
    FooterComponent
  ]
})
export class TeleconsultationListComponent implements OnInit {
  teleconsultations: Teleconsultation[] = [];
  isLoading = false;
  errorMessage = '';
  //displayedColumns: string[] = ['date', 'heure', 'motif', 'status', 'patientId', 'actions'];
  displayedColumns: string[] = ['date', 'heure', 'status', 'patientName', 'doctorName','videoLink', 'actions'];

  doctors: Utilisateur[] = [];
  patients: Utilisateur[] = [];

  constructor(private service: TeleconsultationService, private utilisateurService: UtilisateurService, private router: Router) {}

  ngOnInit(): void {
  this.isLoading = true;
  this.errorMessage = '';

  Promise.all([
    firstValueFrom(this.utilisateurService.getDoctors()),
    firstValueFrom(this.utilisateurService.getPatients())
  ])
  .then(([doctors, patients]) => {
    this.doctors = doctors;
    this.patients = patients;
    this.loadTeleconsultations();
  })
  .catch(() => {
    this.errorMessage = 'Failed to load teleconsultations.';
    this.isLoading = false;
  });
}




  loadTeleconsultations(): void {
  this.isLoading = true;
  this.errorMessage = '';
  const page = 0;
  const size = 10;

  this.service.getAll(page, size).subscribe({
    next: data => {
      this.teleconsultations = data.content; // <--- accès au contenu réel
      this.isLoading = false;
    },
    error: () => {
      this.errorMessage = 'Failed to load teleconsultations.';
      this.isLoading = false;
    }
  });
}



  getStatusLabel(status: string): string {
    const map: any = {
      en_attente: 'En attente',
      confirmee: 'Confirmée',
      terminee: 'Terminée',
      annulee: 'Annulée'
    };
    return map[status] || status;
  }

  getStatusClass(status: string): string {
    return {
      en_attente: 'status-pending',
      confirmee: 'status-confirmed',
      terminee: 'status-completed',
      annulee: 'status-cancelled'
    }[status] || '';
  }

  /*confirmDelete(t: Teleconsultation): void {
  // Vérifie si l'id est défini avant de procéder à la suppression
  if (t.id) {
    const date = t.consultationDate.split('T')[0];
    const heure = t.consultationDate.split('T')[1]?.substring(0, 5);

    if (confirm(`Delete teleconsultation on ${date} at ${heure}?`)) {
      this.service.delete(t.id).subscribe(() => this.loadTeleconsultations());
    }
  } else {
    console.error('Teleconsultation id is undefined.');
  }
}*/
confirmDelete(t: Teleconsultation): void {
  if (!t.id) {
    console.error('Teleconsultation id is undefined.');
    return;
  }

  const date = t.consultationDate.split('T')[0];
  const heure = t.consultationDate.split('T')[1]?.substring(0, 5);

  if (confirm(`Delete teleconsultation on ${date} at ${heure}?`)) {
    this.service.delete(t.id).subscribe(() => this.loadTeleconsultations());
  }
}


  getPatientName(patientId: string): string {
  const patient = this.patients.find(p => p.id === patientId);
  return patient ? patient.username : patientId;
}

getDoctorName(doctorId: string): string {
  const doctor = this.doctors.find(d => d.id === doctorId);
  return doctor ? doctor.username : doctorId;
}

}
