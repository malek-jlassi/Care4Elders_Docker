import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { UtilisateurService } from '../../../../services/utilisateur.service';
import { Utilisateur } from '../../../../models/Utilisateur.model';
import { AppointmentService } from '../../../../services/appointment.service';
import { AuthService } from '../../../../services/Auth.service';
import { Appointment } from '../../../../models/appointment.model';


@Component({
  selector: 'app-appointment-form',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './appointment-form.component.html',
  styleUrl: './appointment-form.component.css'
})
export class AppointmentFormComponent implements OnInit {
  appointmentForm!: FormGroup;
  isEditMode = false;
  appointmentId = '';
  isLoading = false;
  isSubmitting = false;
  errorMessage = '';

  appointmentTypes = [
    { value: 'in_person', label: 'In-Person Visit' },
    { value: 'teleconsultation', label: 'Teleconsultation' }
  ];


  appointmentStatuses = [
    { value: 'pending', label: 'Pending' },
    { value: 'confirmed', label: 'Confirmed' },
    { value: 'cancelled', label: 'Cancelled' },
    { value: 'completed', label: 'Completed' }
  ];

  constructor(
    private fb: FormBuilder,
    private appointmentService: AppointmentService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar,
    private utilisateurService: UtilisateurService,
    private authService: AuthService,
  ) { }

  doctors: Utilisateur[] = [];
  patients: Utilisateur[] = [];

  ngOnInit(): void {
    // Prefill user depending on role
    const currentUserId = this.authService.getCurrentUserId();
    const currentUserData = localStorage.getItem('userData');
    let currentRole:string|null = null;
    if(currentUserData){
      try{currentRole = JSON.parse(currentUserData).role;}catch{currentRole=null;}
    }

    this.initForm();

    // after form created, if user logged in then pre-select patient or doctor
    if (currentUserId) {
      if (currentRole === 'patient') {
        this.appointmentForm.patchValue({ patientId: currentUserId });
        this.appointmentForm.get('patientId')?.disable();
      } else if (currentRole === 'doctor') {
        this.appointmentForm.patchValue({ doctorId: currentUserId });
        this.appointmentForm.get('doctorId')?.disable();
      }
    }
    this.utilisateurService.getDoctors().subscribe(data => this.doctors = data);
    this.utilisateurService.getPatients().subscribe(data => this.patients = data);

    // Check if we're in edit mode
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.appointmentId = params['id'];
        this.loadAppointment(this.appointmentId);
      }
    });
  }

  initForm(): void {
    this.appointmentForm = this.fb.group({
      patientId: ['', [Validators.required]],
      doctorId: ['', [Validators.required]],
       date: ['', [Validators.required, this.dateNotInPastValidator]],
      time: ['', [Validators.required, this.timeNotInPastValidator.bind(this)]],
      durationMinutes: ['', [Validators.required, Validators.min(15), Validators.max(240)]],
      type: ['in_person', [Validators.required]],
      status: ['pending', [Validators.required]],
      reason: ['', [Validators.maxLength(500)]],
      notes: ['', [Validators.maxLength(1000)]]
    });
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
    const selectedDate = this.appointmentForm?.get('date')?.value;
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


  loadAppointment(id: string): void {
    this.isLoading = true;
    this.appointmentService.getById(id).subscribe({
      next: (appointment) => {
        // Split date and time
        const dateObj = new Date(appointment.date);
        const formattedDate = dateObj.toISOString().split('T')[0];
        const formattedTime = dateObj.toTimeString().split(' ')[0].substring(0, 5);

        this.appointmentForm.patchValue({
          patientId: appointment.patientId,
          doctorId: appointment.doctorId,
          date: formattedDate,
          time: formattedTime,
          durationMinutes: appointment.durationMinutes,
          type: appointment.type,
          status: appointment.status,
          reason: appointment.reason || '',
          notes: appointment.notes || ''
        });

        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load appointment details. Please try again.';
        this.isLoading = false;
        console.error('Error loading appointment:', error);
      }
    });
  }

  onSubmit(): void {
    if (this.appointmentForm.invalid) {
      this.markFormGroupTouched(this.appointmentForm);
      return;
    }

    const formValue = this.appointmentForm.value;

    const date = formValue.date; // Date object from mat-datepicker
  const time = formValue.time; // string like "14:30"

  if (!date || !time) {
    this.snackBar.open('Date and time are required', 'Close', {
      duration: 3000,
      panelClass: 'error-snackbar'
    });
    return;
  }

  const [hours, minutes] = time.split(':').map(Number);

  const appointmentDate = new Date(date); // clone to avoid mutating original
  appointmentDate.setHours(hours+1, minutes, 0, 0);

  if (isNaN(appointmentDate.getTime())) {
    this.snackBar.open('Invalid date or time', 'Close', {
      duration: 3000,
      panelClass: 'error-snackbar'
    });
    return;
  }

    const appointmentData: Partial<Appointment> = {
      patientId: formValue.patientId,
      doctorId: formValue.doctorId,
      date:  appointmentDate.toISOString().slice(0, 19),
      durationMinutes: formValue.durationMinutes,
      type: formValue.type,
      status: formValue.status,
      reason: formValue.reason,
      notes: formValue.notes
    };

    this.isSubmitting = true;

    if (this.isEditMode) {
      this.updateAppointment(appointmentData);
    } else {
      this.createAppointment(appointmentData);
    }
  }

  createAppointment(appointmentData: Partial<Appointment>): void {
    try {
      const now = new Date();
      console.log('date ' + now)
      if (isNaN(now.getTime())) {
        throw new Error('Invalid Date');
      }

      appointmentData.createdAt = now.toISOString().slice(0, 19);
      

      this.appointmentService.create(appointmentData as Appointment).subscribe({
        next: () => {
          this.snackBar.open('Appointment successfully created', 'Close', {
            duration: 3000,
            panelClass: 'success-snackbar'
          });
          this.router.navigate(['/appointments/list']);
        },
        error: (error) => {
          this.isSubmitting = false;
          this.snackBar.open('Failed to create appointment', 'Close', {
            duration: 3000,
            panelClass: 'error-snackbar'
          });
          console.error('Error creating appointment:', error);
        }
      });
    } catch (err) {
      console.error('Error generating creation timestamp:', err);
      this.snackBar.open('Failed to create appointment (invalid time)', 'Close', {
        duration: 3000,
        panelClass: 'error-snackbar'
      });
      this.isSubmitting = false;
    }
  }

  updateAppointment(appointmentData: Partial<Appointment>): void {
    const now = new Date();
    if (isNaN(now.getTime())) {
      this.snackBar.open('Failed to update appointment (invalid time)', 'Close', {
        duration: 3000,
        panelClass: 'error-snackbar'
      });
      console.error('Invalid timestamp on update');
      this.isSubmitting = false;
      return;
    }
  
    appointmentData.updatedAt = now.toISOString().slice(0, 19);
    console.log('date updated ' + appointmentData.updatedAt)
  
    this.appointmentService.update(this.appointmentId, appointmentData as Appointment).subscribe({
      next: () => {
        this.snackBar.open('Appointment successfully updated', 'Close', {
          duration: 3000,
          panelClass: 'success-snackbar'
        });
        this.router.navigate(['/appointments/list']);
      },
      error: (error) => {
        this.isSubmitting = false;
        this.snackBar.open('Failed to update appointment', 'Close', {
          duration: 3000,
          panelClass: 'error-snackbar'
        });
        console.error('Error updating appointment:', error);
      }
    });
  }
  

  // Helper to mark all form controls as touched
  markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if ((control as any).controls) {
        this.markFormGroupTouched(control as FormGroup);
      }
    });
  }

  // Convenience getter for form fields
  get f() { return this.appointmentForm.controls; }
}
