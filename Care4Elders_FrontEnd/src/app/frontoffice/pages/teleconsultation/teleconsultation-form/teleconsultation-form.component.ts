import { Component, inject, Injector, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControl, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core'; // important pour le fonctionnement
import { UtilisateurService } from '../../../../services/utilisateur.service';
import { Utilisateur } from '../../../../models/Utilisateur.model';

import { TeleconsultationService } from '../../../../services/teleconsultation.service';
import { MatIcon } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TeleconsultationSummaryDialogComponent } from '../teleconsulation-summary-dialog/teleconsulation-summary-dialog.component';

@Component({
  selector: 'app-teleconsultation-form',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatCardModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIcon,
    MatDialogModule
  ],
  templateUrl: './teleconsultation-form.component.html',
  styleUrls: ['./teleconsultation-form.component.css']
})
export class TeleconsultationFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private service = inject(TeleconsultationService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  //private dialog = inject(MatDialog);

  form: FormGroup =this.fb.group({
  date: ['', [Validators.required, this.dateNotInPastValidator]],
  time: ['', [Validators.required, this.timeNotInPastValidator.bind(this)]],
  durationMinutes: ['', [Validators.required, Validators.min(15), Validators.max(240)]], 
  patientId: ['', Validators.required],
  doctorId: ['', Validators.required],
  notes: [''], 
});

  doctors: Utilisateur[] = [];
  patients: Utilisateur[] = [];
  minDate: Date = new Date();
  isEdit = false;
  id!: string;
 

  constructor(
    private utilisateurService: UtilisateurService,
    private teleService: TeleconsultationService,
    private dialog: MatDialog,
    private injector: Injector
  ) {}

  get f() {
  return this.form.controls;
  }

  // 🔴 Custom validator - Date must not be in the past
  dateNotInPastValidator(control: AbstractControl): ValidationErrors | null {
    const selectedDate = new Date(control.value);
    const today = new Date();
    selectedDate.setHours(0, 0, 0, 0);
    today.setHours(0, 0, 0, 0);
    return selectedDate < today ? { dateInPast: true } : null;
  }

  // 🔴 Custom validator - Time must not be in the past if date is today
  timeNotInPastValidator(control: AbstractControl): ValidationErrors | null {
    const selectedTime = control.value;
    const selectedDate = this.form?.get('date')?.value;
    if (!selectedDate || !selectedTime) return null;

    const selectedDateTime = new Date(selectedDate);
    const [hours, minutes] = selectedTime.split(':');
    selectedDateTime.setHours(+hours, +minutes, 0);

    const now = new Date();

    if (
      new Date(selectedDate).toDateString() === now.toDateString() &&
      selectedDateTime < now
    ) {
      return { timeInPast: true };
    }

    return null;
  }

  ngOnInit(): void {
    // Charger les listes
    this.utilisateurService.getDoctors().subscribe(data => this.doctors = data);
    this.utilisateurService.getPatients().subscribe(data => this.patients = data);

    this.route.params.subscribe(params => {
      if (params['id']) {
        this.id = params['id'];
        this.isEdit = true;
        this.service.getById(this.id).subscribe(tc => this.form.patchValue(tc));
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    const formValues = this.form.value;

    const [hour, minute] = formValues.time.split(':');
    const datetime = new Date(formValues.date);
    datetime.setHours(+hour, +minute, 0);

    const body = {
      consultationDate: datetime.toISOString(),
      durationMinutes: formValues.durationMinutes,
      doctorId: formValues.doctorId,
      patientId: formValues.patientId,
      status: formValues.status,
      notes: formValues.notes
    };

    const action = this.isEdit
      ? this.service.update(this.id, body)
      : this.service.add(body);

    action.subscribe({
      next: (response) => {
        // Récupère les noms des utilisateurs à partir des IDs
        this.utilisateurService.getById(response.doctorId).subscribe(doctor => {
          this.utilisateurService.getById(response.patientId).subscribe(patient => {
            const dialogRef = this.dialog.open(TeleconsultationSummaryDialogComponent, {
              width: '400px',
              data: {
                ...response,
                doctorName: doctor.username,
                patientName: patient.username
              },
              //injector: this.injector
            });

            dialogRef.afterClosed().subscribe(() => {
              this.router.navigate(['/teleconsultations']);
            });
          });
        });
      },
      error: (err) => console.error('Saving error:', err)
    });
  }

  openTestDialog() {
    this.dialog.open(TeleconsultationSummaryDialogComponent, {
      data: {
        doctorId: 'fake',
        patientId: 'fake',
        consultationDate: new Date().toISOString(),
        durationMinutes: 30,
        notes: 'test',
        videoLink: 'https://example.com',
        doctorName: 'Dr Test',
        patientName: 'Patient Test'
      }
    });
  }
}
