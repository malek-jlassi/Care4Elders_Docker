import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { UtilisateurService } from '../../../../services/utilisateur.service';
import { Utilisateur } from '../../../../models/Utilisateur.model';
import { AppointmentService } from '../../../../services/appointment.service';
import { Appointment } from '../../../../models/appointment.model';

import { firstValueFrom } from 'rxjs';
import { ConfirmDialogComponent } from '../../../../shared/confirm-dialog/confirm-dialog.component';
import { FooterComponent } from '../../../../shared/layout/footer/footer.component';
import { NavbarComponent } from '../../../../shared/layout/navbar/navbar.component';
import { TopbarComponent } from '../../../../shared/layout/topbar/topbar.component';

@Component({
  selector: 'app-appointment-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatSnackBarModule,
    MatDialogModule,
    TopbarComponent,
    NavbarComponent,
    FooterComponent
  ],
  templateUrl: './appointment-list.component.html',
  styleUrl: './appointment-list.component.css'
})
export class AppointmentListComponent implements OnInit {
  appointments: Appointment[] = [];
  displayedColumns: string[] = ['date', 'patientId', 'doctorId', 'type', 'status', 'durationMinutes', 'actions'];
  isLoading = true;
  errorMessage = '';

  constructor(
    private appointmentService: AppointmentService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private utilisateurService: UtilisateurService,
  ) { }

  doctors: Utilisateur[] = [];
  patients: Utilisateur[] = [];

  ngOnInit(): void {
  this.isLoading = true;

  Promise.all([
    firstValueFrom(this.utilisateurService.getDoctors()),
    firstValueFrom(this.utilisateurService.getPatients())
  ])
  .then(([doctors, patients]) => {
    this.doctors = doctors;
    this.patients = patients;
    this.loadAppointments();
  })
  .catch(() => {
    this.errorMessage = 'Failed to load appointments. Please try again later.';
    this.isLoading = false;
  });
}

getPatientName(patientId: string): string {
  const patient = this.patients.find(p => p.id === patientId);
  return patient ? patient.username : patientId;
}

getDoctorName(doctorId: string): string {
  const doctor = this.doctors.find(d => d.id === doctorId);
  return doctor ? doctor.username : doctorId;
}

  loadAppointments(): void {
    this.isLoading = true;
    this.appointmentService.getAll().subscribe({
      next: (data) => {
        this.appointments = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load appointments. Please try again later.';
        this.isLoading = false;
        console.error('Error loading appointments:', error);
      }
    });
  }

  getFormattedDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleString();
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'confirmed': return 'status-confirmed';
      case 'pending': return 'status-pending';
      case 'cancelled': return 'status-cancelled';
      case 'completed': return 'status-completed';
      default: return '';
    }
  }

  confirmDelete(appointment: Appointment): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Confirm Deletion',
        message: `Are you sure you want to delete the appointment on ${this.getFormattedDate(appointment.date)}?`,
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteAppointment(appointment.id);
      }
    });
  }

  deleteAppointment(id: string): void {
    this.appointmentService.delete(id).subscribe({
      next: () => {
        this.snackBar.open('Appointment successfully deleted', 'Close', {
          duration: 3000,
          panelClass: 'success-snackbar'
        });
        this.loadAppointments();
      },
      error: (error) => {
        this.snackBar.open('Failed to delete appointment', 'Close', {
          duration: 3000,
          panelClass: 'error-snackbar'
        });
        console.error('Error deleting appointment:', error);
      }
    });
  }
}
