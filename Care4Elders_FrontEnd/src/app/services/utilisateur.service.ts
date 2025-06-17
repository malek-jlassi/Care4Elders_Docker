import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { Utilisateur } from '../models/Utilisateur.model';

@Injectable({
  providedIn: 'root'
})
export class UtilisateurService {
  private apiUrl = 'http://localhost:8080/api/utilisateur';

  constructor(private http: HttpClient) {}

  inscrireUtilisateur(formData: FormData): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/inscription`, formData);
  }
  
  getAll(): Observable<Utilisateur[]> {
    return this.http.get<Utilisateur[]>(`${this.apiUrl}/afficherListeUtilisateurs`);
  }

  getPatients(): Observable<Utilisateur[]> {
    return this.getAll().pipe(map(users => users.filter(u => u.role === 'PATIENT')));
  }

  getDoctors(): Observable<Utilisateur[]> {
    return this.getAll().pipe(map(users => users.filter(u => u.role === 'MEDECIN')));
  }

  getById(id: string): Observable<Utilisateur> {
    return this.http.get<Utilisateur>(`${this.apiUrl}/afficherUtilisateurParId/${id}`);
  }

  ajouterUtilisateur(utilisateur: Utilisateur): Observable<Utilisateur> {
    return this.http.post<Utilisateur>(`${this.apiUrl}/ajouterUtilisateur`, utilisateur);
  }

 updateUtilisateur(formData: FormData): Observable<any> {
  return this.http.put<any>(`${this.apiUrl}/mettreAJourUtilisateur`, formData);
}


  updateUtilisateurParId(id: string, utilisateur: Utilisateur): Observable<Utilisateur> {
    return this.http.put<Utilisateur>(`${this.apiUrl}/mettreAJourUtilisateurParId/${id}`, utilisateur);
  }

  deleteUtilisateur(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/supprimerUtilisateur/${id}`);
  }

  modifierMotDePasse(id: string, ancienMotDePasse: string, nouveauMotDePasse: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/modifierMotDePasse/${id}`, {
      ancienMotDePasse,
      nouveauMotDePasse
    });
  }
}
