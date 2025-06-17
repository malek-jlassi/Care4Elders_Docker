import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Utilisateur } from '../../models/Utilisateur.model';
import { UtilisateurService } from '../../services/utilisateur.service';
import { HttpClientModule } from '@angular/common/http';


@Component({
  selector: 'app-list-utilisateur',
  standalone: true,
  imports: [CommonModule,HttpClientModule],
  templateUrl: './list-utilisateur.component.html',
  styleUrl: './list-utilisateur.component.css'
})
export class ListUtilisateurComponent {
  utilisateurs: Utilisateur[] = [];

  selectedImageUrl: string = 'https://bootdey.com/img/Content/avatar/avatar7.png'; // Valeur par défaut


  constructor(private utilisateurService: UtilisateurService) {}

  ngOnInit(): void {
    this.utilisateurService.getAll().subscribe(data => {
      this.utilisateurs = data;
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
  
    if (input.files && input.files[0]) {
      const file = input.files[0];
      
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.selectedImageUrl = e.target.result; // Affiche l'image localement
      };
      reader.readAsDataURL(file);
      
      // TODO: envoyer le fichier au backend si nécessaire
      // this.uploadImage(file);
    }
  }
}


