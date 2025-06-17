import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
//import { PasswordResetRequest } from '../models/PasswordResetRequest.model'; 


@Injectable({
  providedIn: 'root'
})
export class EmailService {
  private apiUrl = 'http://localhost:8080/api/Email';

  constructor(private http: HttpClient) {}

  //resetPasswordEmail(email: string): Observable<any> {
   // return this.http.post(`${this.apiUrl}/resetPasswordEmail`, { email });
  //}

  resetPasswordEmail(email: string): Observable<string> {
    return this.http.post(this.apiUrl + '/resetPasswordEmail', { email }, { responseType: 'text' });
  }

  updatePassword(token: string, nouveauMotDePasse: string): Observable<string> {
    return this.http.post(`${this.apiUrl}/updatePassword`, { token, nouveauMotDePasse }, { responseType: 'text' });
  }
  
}
