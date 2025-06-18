import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, map } from 'rxjs';

export interface UserDTO {
  id: string;
  name: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly API_URL = 'http://localhost:8080/api/utilisateur';

  constructor(private http: HttpClient, private router: Router) {}

  getAllUsers(): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(`${this.API_URL}/afficherListeUtilisateurs`);
  }

  getPatients(): Observable<UserDTO[]> {
    return this.getAllUsers().pipe(map((users: UserDTO[]) => users.filter((u: UserDTO) => u.role === 'patient')));
  }

  getDoctors(): Observable<UserDTO[]> {
    return this.getAllUsers().pipe(map((users: UserDTO[]) => users.filter((u: UserDTO) => u.role === 'doctor')));
  }

  getUserById(id: string): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.API_URL}/afficherUtilisateurParId/${id}`);
  }

  // Store user info in localStorage
  setUser(user: UserDTO): void {
    localStorage.setItem('currentUser', JSON.stringify(user));
  }

  // Retrieve current user info
  getUser(): UserDTO | null {
    const userStr = localStorage.getItem('currentUser');
    return userStr ? JSON.parse(userStr) : null;
  }

  // Remove user info and clear session
  logout(): void {
    // Clear all localStorage and sessionStorage for a full app data reset
    localStorage.clear();
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }
}