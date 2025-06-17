import { Component ,OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router,RouterModule } from '@angular/router';
import { EmailService } from '../../../services/email.service';
import { ActivatedRoute } from '@angular/router';


@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css'
})
export class ForgotPasswordComponent implements OnInit{
  step: number = 1;
  emailForm: FormGroup;
  resetForm: FormGroup;
  successMessage: string = '';
  errorMessage: string = '';
  token: string | null = null;
showResetForm: boolean = false;

  constructor(
    private fb: FormBuilder,
    private emailService: EmailService,
    private router: Router,
    private route: ActivatedRoute // <<--- ICI
  ) {
    this.emailForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  
    this.resetForm = this.fb.group({
      token: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6)]]
    });
  }
  
  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      console.log('Token détecté :', token); // 👈 Debug
  
      if (token && token.trim() !== '') {
        this.step = 2;
        this.resetForm.patchValue({ token: token });
      } else {
        this.step = 1;
      }
  
      console.log('Step actuel :', this.step); // 👈 Debug
    });
  }
  
  
  
  
  

 /* sendToken(): void {
    this.successMessage = '';
    this.errorMessage = '';
    if (this.emailForm.valid) {
      const email = this.emailForm.value.email;
      this.emailService.resetPasswordEmail(email).subscribe({
        next: () => {
          this.step = 2;
          this.successMessage = "📧 Un lien de réinitialisation a été envoyé à votre adresse email.";
        },
        error: (err) => {
          this.errorMessage = "❌ Échec de l'envoi de l'email. Veuillez réessayer.";
          console.error(err);
        }
      });
    }
  }*/
    sendToken(): void {
      this.successMessage = '';
      this.errorMessage = '';
    
      if (this.emailForm.valid) {
        const email = this.emailForm.value.email;
        this.emailService.resetPasswordEmail(email).subscribe({
          next: (response: string) => {
            console.log(response);
            this.successMessage = "📧 Un lien de réinitialisation a été envoyé à votre adresse email.";
            // ⛔ Ne pas forcer step ici
          },
          error: (error) => {
            console.error(error);
            this.errorMessage = "❌ Échec de l'envoi de l'email. Veuillez réessayer.";
          }
        });
      }
    }
    

    resetPassword(): void {
      this.successMessage = '';
      this.errorMessage = '';
    
      if (this.resetForm.valid) {
        const { token, newPassword } = this.resetForm.value;
        this.emailService.updatePassword(token, newPassword).subscribe({
          next: (response: string) => {
            console.log(response); // Afficher la réponse du backend
            this.successMessage = "🔑 Votre mot de passe a été réinitialisé avec succès.";
          },
          error: (error) => {
            console.error(error); // Afficher l'erreur si nécessaire
            this.errorMessage = "❌ Une erreur est survenue lors de la réinitialisation du mot de passe.";
          }
        });
      }
    }

    goToLogin() {
      this.router.navigate(['/login']); // adapte le chemin si nécessaire
    
}
}
