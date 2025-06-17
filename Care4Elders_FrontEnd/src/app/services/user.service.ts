import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
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

  constructor(private http: HttpClient) {}

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

}