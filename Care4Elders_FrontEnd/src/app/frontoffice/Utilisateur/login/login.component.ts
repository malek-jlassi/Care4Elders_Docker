import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
//import { AuthService } from '../../../services/Auth.service';
import { TopbarComponent } from '../../../shared/layout/topbar/topbar.component';
import { NavbarComponent } from '../../../shared/layout/navbar/navbar.component';
import { FooterComponent } from '../../../shared/layout/footer/footer.component';
import { AuthService } from '../../../services/Auth.service';
import { UtilisateurService } from '../../../services/utilisateur.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    TopbarComponent,
    NavbarComponent,
    FooterComponent
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage = '';
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private utilisateurService: UtilisateurService
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;
      this.isLoading = true;
      this.errorMessage = '';

      this.authService.login({ email, password }).subscribe({
        next: (response) => {
          this.isLoading = false;
          // If response already contains full user info (id, name, role), save directly
          if (response && response.id && response.name && response.role) {
            this.utilisateurService.setUser(response);
            this.router.navigate(['/']);
          } else if (response && response.id) {
            // Otherwise, fetch user details using the returned userId
            this.utilisateurService.getById(response.id).subscribe({
              next: (user) => {
                this.utilisateurService.setUser(user);
                this.router.navigate(['/']);
              },
              error: () => {
                this.errorMessage = 'Failed to retrieve user details after login.';
              }
            });
          } else {
            this.errorMessage = 'Invalid login response from server.';
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Login failed. Please try again.';
        }
      });
    } else {
      this.errorMessage = 'Please fill in all required fields correctly.';
    }
  }
}
