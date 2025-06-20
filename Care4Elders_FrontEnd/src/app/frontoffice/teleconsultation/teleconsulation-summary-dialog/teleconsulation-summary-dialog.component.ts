import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { UtilisateurService } from '../../../services/utilisateur.service';
import { Utilisateur } from '../../../models/Utilisateur.model';
import { TeleconsultationService } from '../../../services/teleconsultation.service';
import { CommonModule, DatePipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-teleconsultation-summary-dialog',
  standalone: true,
  imports:[CommonModule,MatDialogModule,MatButtonModule,MatProgressSpinnerModule],
  templateUrl: './teleconsultation-summary-dialog.component.html',
  styleUrls: ['./teleconsultation-summary-dialog.component.css']
})
export class TeleconsultationSummaryDialogComponent implements OnInit {
  doctorName: string = '';
  patientName: string = '';
  loading: boolean = false;
  error: string | null = null;

  constructor(
    private utilisateurService: UtilisateurService,
    private teleService: TeleconsultationService,
    private dialogRef: MatDialogRef<TeleconsultationSummaryDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      doctorId: string;
      patientId: string;
      consultationDate: string;
      durationMinutes: number;
      notes: string;
      videoLink: string;
      doctorName?: string;
      patientName?: string;
    }
  ) {}

  ngOnInit(): void {
    this.loading=true;
    console.log(' Dialog opened successfully');
    // Si les noms sont déjà fournis depuis le formulaire, on les utilise directement
    this.doctorName = this.data.doctorName  || 'Unknown doctor';
    this.patientName = this.data.patientName || 'Unknown patient';
}

  /*ngOnInit(): void {

    this.loading=true;

    this.utilisateurService.getById(this.data.doctorId).subscribe({
      next: (user: Utilisateur) => this.doctorName = user.username,
      error: () => this.doctorName = 'Unknown doctor'
    });

    this.utilisateurService.getById(this.data.patientId).subscribe({
      next: (user: Utilisateur) => this.patientName = user.username,
      error: () => this.patientName = 'Unknown patient'
    });
  }*/

  confirm(): void {
    this.loading = true;
    this.teleService.add(this.data).subscribe({
      next: () => {
        this.loading = false;
        this.dialogRef.close(true);  // Renvoie confirmation au composant parent
      },
      error: err => {
        this.loading = false;
        this.error = 'Error while creating teleconsultation';
        console.error(err);
      }
    });
  }
}