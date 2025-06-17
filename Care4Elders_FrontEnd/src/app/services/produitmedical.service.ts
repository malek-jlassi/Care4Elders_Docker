import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Produit } from '../models/produitmedical.model';

@Injectable({
  providedIn: 'root'
})
export class ProduitMedicalService {

  private apiUrl = 'http://localhost:8080/produits'; 

  constructor(private http: HttpClient) { }

  ajouterProduitAvecOCR(produitData: FormData): Observable<Produit> {
    return this.http.post<Produit>(`${this.apiUrl}/ajouterProduitAvecOCR`, produitData);
  }

  modifierProduit(id: string, formData: FormData): Observable<any> {
    return this.http.put(`${this.apiUrl}/modifierProduit/${id}`, formData);
  }
  

  getAllProduits(): Observable<Produit[]> {
    return this.http.get<Produit[]>(`${this.apiUrl}/ListerTousProduits`);
  }

  getProduit(id: string): Observable<Produit> {
    return this.http.get<Produit>(`${this.apiUrl}/ListerProduit/${id}`);
  }

  supprimerProduit(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/supprimerProduit/${id}`);
  }

  filtrerParPrix(minPrix: number, maxPrix: number): Observable<Produit[]> {
    let params = new HttpParams()
      .set('minPrix', minPrix.toString())
      .set('maxPrix', maxPrix.toString());
    return this.http.get<Produit[]>(`${this.apiUrl}/filtrerParPrix`, { params });
  }

  trierParNom(direction: 'asc' | 'desc'): Observable<Produit[]> {
    return this.http.get<Produit[]>(`${this.apiUrl}/trierParNomm?direction=${direction}`);
  }
}
