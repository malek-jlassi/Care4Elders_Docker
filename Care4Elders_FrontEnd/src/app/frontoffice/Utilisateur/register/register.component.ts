import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Role, Utilisateur } from '../../../models/Utilisateur.model';
import { Router, RouterModule } from '@angular/router';
import { UtilisateurService } from '../../../services/utilisateur.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerForm: FormGroup;
  roles = Object.values(Role).filter(role => role !== 'ADMIN');
  showPassword = false;
  showConfirmPassword = false;

  constructor(
    private fb: FormBuilder,
    private utilisateurService: UtilisateurService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', [Validators.required, Validators.pattern('^[0-9]{8,15}$')]],
      dateOfBirth: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
      role: ['', Validators.required],

      // Champs spécifiques
      adressePatient: [''],
      dossierMedicalPdf: [null],
      adresseAidant: [''],
      cartedIentite: [null],
      specialite: [''],
      numeroOrdre: [''],
      fichierDiplome: [null]
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password')?.value;
    const confirm = form.get('confirmPassword')?.value;
    return password === confirm ? null : { mismatch: true };
  }

  togglePasswordVisibility(field: string) {
    if (field === 'password') {
      this.showPassword = !this.showPassword;
    } else if (field === 'confirm') {
      this.showConfirmPassword = !this.showConfirmPassword;
    }
  }

  onSubmit() {
    if (this.registerForm.valid) {
      const formValues = this.registerForm.value;
      
      // Créer un objet FormData pour envoyer le formulaire
      const formData = new FormData();
      formData.append('username', formValues.username);
      formData.append('email', formValues.email);
      formData.append('phoneNumber', formValues.phoneNumber);
      formData.append('dateOfBirth', formValues.dateOfBirth);
      formData.append('password', formValues.password);
      formData.append('role', formValues.role);

      // Champs spécifiques dépendant du rôle
      switch (formValues.role) {
        case 'PATIENT':
          if (formValues.adressePatient) formData.append('adressePatient', formValues.adressePatient);
          if (formValues.dossierMedicalPdf) formData.append('dossierMedicalPdf', formValues.dossierMedicalPdf);
          break;
        case 'AIDANT':
          if (formValues.adresseAidant) formData.append('adresseAidant', formValues.adresseAidant);
          if (formValues.cartedIentite) formData.append('cartedIentite', formValues.cartedIentite);
          break;
        case 'MEDECIN':
          if (formValues.specialite) formData.append('specialite', formValues.specialite);
          if (formValues.numeroOrdre) formData.append('numeroOrdre', formValues.numeroOrdre);
          if (formValues.fichierDiplome) formData.append('fichierDiplome', formValues.fichierDiplome);
          break;
      }

      this.utilisateurService.inscrireUtilisateur(formData).subscribe({
        next: (res) => {
          console.log('✅ Inscription réussie :', res);
          this.router.navigate(['/login']);
        },
        error: (err) => {
          console.error('❌ Erreur lors de l’inscription :', err);
        }
      });
    } else {
      console.log('🔴 Formulaire invalide');
      this.registerForm.markAllAsTouched();
    }
  }

  onFileChange(event: Event, controlName: string) {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files.length > 0) {
      const file = fileInput.files[0];
      this.registerForm.get(controlName)?.setValue(file);
    }
  }
  navigateToLogin() {
    this.router.navigate(['/login']);
  }
}
