import { Component, OnInit ,Output, EventEmitter} from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators,ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { CareRequestService } from '../../../../services/care-request.service';
import { CareRequest } from '../../../../models/care-request.model';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../../services/Auth.service';

//import { CreateCareRequestModalComponent } from '../create-care-request-modal/create-care-request-modal.component';



function arrayRequired(control: AbstractControl): ValidationErrors | null {
  return control.value && control.value.length > 0 ? null : { required: true };
}

function notBeforeNow(control: AbstractControl): ValidationErrors | null {
  const value = control.value;
  if (!value) return null;
  const selectedDate = new Date(value);
  const now = new Date();

  return selectedDate >= now ? null : { pastDate: true };
}

function endDateAfterStartDate(control: AbstractControl): ValidationErrors | null {
  const startDate = control.get('startDate')?.value;
  const endDate = control.get('endDate')?.value;

  if (startDate && endDate && new Date(endDate) <= new Date(startDate)) {
    return { endDateBeforeStartDate: true };
  }
  
  return null;
}


@Component({
  standalone: true,
  selector: 'app-care-request-form',
  templateUrl: './care-request-form.component.html',
  imports: [
    CommonModule,
    ReactiveFormsModule
  ]
})
export class CareRequestFormComponent implements OnInit {
  form!: FormGroup;
  careTypes = ['VISITE_MEDECIN', 'REPAS' , 'BAIN' ,'MENAGE'];
  statuses = ['EN_ATTENTE', 'AFFECTE', 'TERMINE' , 'ANNULE'];
  maladiesOptions = ['DIABETE', 'ASTHME', 'HYPERTENSION', 'CANCER', 'CARDIOPATHIE', 'AUTRE'];
  careRequests: any[] = [];
  patientInfo: any;
  currentDateTime: string = '';
  endDateMin: string = '';
  editingRequest?: CareRequest;

  constructor(
    private fb: FormBuilder,
    private careService: CareRequestService,
    private http: HttpClient,
    private authService: AuthService,
    private router: Router
  ) {}

  @Output() requestCreated = new EventEmitter<void>();
  
  // ngOnInit(): void {
  //   this.updateMinDate();
  //   setInterval(() => this.updateMinDate(), 60000);
  //   this.form = this.fb.group({
  //     maladiesChroniques: [[], arrayRequired],
  //     careType: ['', Validators.required],
  //     description: ['', [Validators.required, Validators.minLength(10)]],
  //     startDate: ['', [Validators.required, notBeforeNow]],
  //     endDate: ['', [Validators.required, endDateAfterStartDate ]]
  //   });
  
  //   const patientId = this.authService.getLoggedInUserId();
  //   this.getPatientInfo(patientId);
  // }
  ngOnInit(): void {
    this.updateMinDate();
    setInterval(() => this.updateMinDate(), 60000);
  
    this.form = this.fb.group({
      maladiesChroniques: [[], arrayRequired],
      careType: ['', Validators.required],
      description: ['', [Validators.required, Validators.minLength(10)]],
      startDate: ['', [Validators.required, notBeforeNow]],
      endDate: ['', [Validators.required, endDateAfterStartDate ]]
    });
  
    // 🔁 Subscribe to current user
    this.authService.currentUser$.subscribe(user => {
      if (user && user.id) {
        this.getPatientInfo(user.id);
      } else {
        console.error('Utilisateur non connecté');
        // Optionally: redirect to login page or show error message
      }
    });
  }
  
  
  updateMinDate(): void {
    const now = new Date();
  
    const pad = (n: number) => n.toString().padStart(2, '0');
  
    const year = now.getFullYear();
    const month = pad(now.getMonth() + 1); // mois commence à 0
    const day = pad(now.getDate());
    const hours = pad(now.getHours());
    const minutes = pad(now.getMinutes());
  
    this.currentDateTime = `${year}-${month}-${day}T${hours}:${minutes}`;
    this.endDateMin = this.currentDateTime;
  }
  
  
  
  getPatientInfo(id: string): void {
    this.http.get(`http://localhost:9090/api/utilisateur/afficherUtilisateurParId/${id}`).subscribe({
      next: data => {
        this.patientInfo = data;
        console.log('Patient Info:', this.patientInfo);
      },
      error: err => console.error('Failed to load patient info', err)
    });
  }
  
  
  setRequestToEdit(request: CareRequest): void {
    this.editingRequest = request;
    this.form.patchValue({
      maladiesChroniques: request.maladiesChroniques || [],
      careType: request.careType,
      description: request.description,
      startDate: request.startDate,
      endDate: request.endDate
    });
  }

  
  
  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const formValue = this.form.value;
    const start = new Date(this.form.value.startDate);
    const end = new Date(this.form.value.endDate);
    
    if (end <= start) {
      alert('End date must be after start date.');
      return;
    }

    // Convert dates to ISO string and remove milliseconds and timezone
    const formatDate = (date: Date) => {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      return `${year}-${month}-${day}T${hours}:${minutes}:00`;
    };

    // Log the payload for debugging
    console.log('Form Values:', formValue);
    console.log('Patient Info:', this.patientInfo);

    const payload = {
      id: this.editingRequest?.id,
      patient: {
        id: this.patientInfo.id,
        username: this.patientInfo.username,
        adressePatient: this.patientInfo.adressePatient || '',
        phoneNumber: parseInt(this.patientInfo.phoneNumber),
        email: this.patientInfo.email
      },
      maladiesChroniques: formValue.maladiesChroniques || [],
      careType: formValue.careType,
      description: formValue.description,
      startDate: formatDate(new Date(formValue.startDate)),
      endDate: formatDate(new Date(formValue.endDate)),
      status: 'EN_ATTENTE',
      dateCreation: this.editingRequest?.dateCreation || formatDate(new Date()),
      dateUpdate: formatDate(new Date())
    };

    // Log the final payload
    console.log('Sending payload:', JSON.stringify(payload, null, 2));
  
    if (this.editingRequest) {
      // Existing edit logic (keep as is)
      this.http.put(`${this.careService.getBaseUrl()}/${this.editingRequest.id}`, payload).subscribe({
        next: res => {
          this.form.reset({
            maladiesChroniques: [],
            careType: '',
            description: '',
            startDate: '',
            endDate: ''
          });
          this.editingRequest = undefined;
          this.requestCreated.emit();
        },
        error: err => {
          console.error('Error details:', err);
          const errorMessage = err.error?.message || 'An error occurred while saving the care request. Please try again.';
          alert(`Error: ${errorMessage}`);
        }
      });
    } else {
      // New care request: use service and redirect to facture
      this.careService.create(payload).subscribe({
        next: (res) => {
          console.log('Success response:', res);
          this.form.reset({
            maladiesChroniques: [],
            careType: '',
            description: '',
            startDate: '',
            endDate: ''
          });
          this.editingRequest = undefined;
          this.requestCreated.emit();
          if (res && res.facture && res.facture.id) {
            this.router.navigate(['/facture-detail', res.facture.id]);
          }
        },
        error: err => {
          console.error('Error details:', err);
          const errorMessage = err.error?.message || 'An error occurred while saving the care request. Please try again.';
          alert(`Error: ${errorMessage}`);
        }
      });
    }
  }

  
  onMaladieChange(event: any): void {
    const value = event.target.value;
    const selected = this.form.get('maladiesChroniques')!.value as string[];
    if (event.target.checked) {
      this.form.get('maladiesChroniques')!.setValue([...selected, value]);
    } else {
      this.form.get('maladiesChroniques')!.setValue(selected.filter(m => m !== value));
    }
  }

  
}
