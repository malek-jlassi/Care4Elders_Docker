// auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/auth';
  private readonly USER_API_URL = 'http://localhost:8080/api/utilisateur';
  private currentUserSubject = new BehaviorSubject<any>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    // Try to get user data from localStorage on service initialization
    const userData = localStorage.getItem('userData');
    if (userData) {
      this.currentUserSubject.next(JSON.parse(userData));
    }
  }

  login(credentials: { email: string; password: string }): Observable<any> {
    return this.http.post(`${this.API_URL}/login`, credentials).pipe(
      tap((response: any) => {
        if (response && response.id) {
          localStorage.setItem('userData', JSON.stringify(response));
          this.currentUserSubject.next(response);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem('userData');
    this.currentUserSubject.next(null);
  }

  isLoggedIn(): boolean {
    return this.currentUserSubject.value !== null;
  }

  getCurrentUserId(): string | null {
    const currentUser = this.currentUserSubject.value;
    return currentUser ? currentUser.id : null;
  }

  getUserDetails(userId: string): Observable<any> {
    return this.http.get(`${this.USER_API_URL}/afficherUtilisateurParId/${userId}`);
  }
}
