import { Component, OnInit } from '@angular/core';
import { UtilisateurService } from '../../../../services/utilisateur.service';

import { Router } from '@angular/router';

import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { firstValueFrom } from 'rxjs';

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
  constructor(private utilisateurService: UtilisateurService, private teleconsultationService: TeleconsultationService, private router: Router) {}

  teleconsultations: Teleconsultation[] = [];
  isLoading = false;
  errorMessage = '';
  //displayedColumns: string[] = ['date', 'heure', 'motif', 'status', 'patientId', 'actions'];
  displayedColumns: string[] = ['date', 'heure', 'status', 'patientName', 'doctorName','videoLink', 'actions'];

  doctors: Utilisateur[] = [];
  patients: Utilisateur[] = [];

  ngOnInit(): void {
    const user = this.utilisateurService.getUser();
    if (user && user.id) {
      this.fetchTeleconsultations(user.id);
    } else {
      // Not logged in: teleconsultations should be empty and loading should stop
      this.teleconsultations = [];
      this.isLoading = false;
    }
  }

  fetchTeleconsultations(userId: string): void {
    this.isLoading = true;
    this.errorMessage = '';
    if (!userId) {
      // Not logged in: teleconsultations should be empty and loading should stop
      this.teleconsultations = [];
      this.isLoading = false;
      return;
    }
    this.teleconsultationService.getTeleconsultationsByUser(userId).subscribe({
      next: (data) => {
        this.teleconsultations = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load teleconsultations from server.';
        this.isLoading = false;
        console.error('Error loading teleconsultations:', error);
      }
    });
  }

  loadTeleconsultations(): void {
    const user = this.utilisateurService.getUser();
    if (user && user.id) {
      this.fetchTeleconsultations(user.id);
    } else {
      // Not logged in: teleconsultations should be empty and loading should stop
      this.teleconsultations = [];
      this.isLoading = false;
    }
  }

  getFormattedDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleString();
  }


  deleteTeleconsultation(id: string): void {
    this.teleconsultationService.delete(id).subscribe({
      next: () => {
        this.loadTeleconsultations();
      },
      error: () => {
        this.errorMessage = 'Failed to delete teleconsultation.';
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

  confirmDelete(teleconsultation: Teleconsultation): void {
    if (!teleconsultation.id) {
      console.error('Teleconsultation id is undefined.');
      return;
    }
    const date = teleconsultation.consultationDate.split('T')[0];
    const heure = teleconsultation.consultationDate.split('T')[1]?.substring(0, 5);
    if (confirm(`Delete teleconsultation on ${date} at ${heure}?`)) {
      this.teleconsultationService.delete(teleconsultation.id).subscribe(() => this.loadTeleconsultations());
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
