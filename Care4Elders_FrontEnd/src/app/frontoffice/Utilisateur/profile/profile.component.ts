import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { UtilisateurService } from '../../../services/utilisateur.service';
import { Role, Utilisateur } from '../../../models/Utilisateur.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  profilForm!: FormGroup;
  role!: Role;
  utilisateurId!: string;
  selectedFiles: { [key: string]: File } = {};
  previewUrls: { [key: string]: string | ArrayBuffer | null } = {};
  isLoading: boolean = false;
  avatarPreview: string | ArrayBuffer | null = null;
  

  constructor(
    private fb: FormBuilder,
    private utilisateurService: UtilisateurService,
    private router: Router
  ) {}
  
  ngOnInit(): void {
    this.utilisateurId = localStorage.getItem('utilisateurId') || '';
    console.log('Utilisateur ID récupéré:', this.utilisateurId);

    if (this.utilisateurId) {
      this.utilisateurService.getById(this.utilisateurId).subscribe({
        next: (utilisateur) => {
          console.log('Utilisateur récupéré:', utilisateur);
          this.role = utilisateur.role;
          this.initForm(utilisateur);
          this.avatarPreview = utilisateur.cartedIentite || 'https://bootdey.com/img/Content/avatar/avatar7.png';

          // Initialiser les previews PDF si existants
          if (utilisateur.dossierMedicalPdf) {
            this.previewUrls['dossierMedicalPdf'] = utilisateur.dossierMedicalPdf;
          }
          if (utilisateur.fichierDiplome) {
            this.previewUrls['fichierDiplome'] = utilisateur.fichierDiplome;
          }
          if (utilisateur.cartedIentite) {
            this.previewUrls['cartedIentite'] = utilisateur.cartedIentite;
          }
        },
        error: (error) => {
          console.error('Erreur lors de la récupération de l\'utilisateur:', error);
        }
      });
    } else {
      console.error('Utilisateur ID est undefined ou vide');
    }
  }

  initForm(utilisateur: Utilisateur): void {
    this.profilForm = this.fb.group({
      username: [utilisateur.username || '', Validators.required],
      email: [utilisateur.email || '', [Validators.required, Validators.email]],
      phoneNumber: [utilisateur.phoneNumber || '', Validators.required],
      dateOfBirth: [utilisateur.dateOfBirth || '', Validators.required],
      password: [utilisateur.password || '', Validators.required],
      role: [utilisateur.role || '', Validators.required],
      adressePatient: [utilisateur.adressePatient || ''],
      dossierMedicalPdf: [utilisateur.dossierMedicalPdf || ''],
      adresseAidant: [utilisateur.adresseAidant || ''],
      cartedIentite: [utilisateur.cartedIentite || ''],
      specialite: [utilisateur.specialite || ''],
      numeroOrdre: [utilisateur.numeroOrdre || ''],
      fichierDiplome: [utilisateur.fichierDiplome || '']
    });
  }

  onFileSelected(event: Event, fieldName: string = 'avatar'): void {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      const file = target.files[0];
      if (fieldName === 'avatar') {
        this.selectedFiles['avatar'] = file;
        const reader = new FileReader();
        reader.onload = () => this.avatarPreview = reader.result;
        reader.readAsDataURL(file);
      } else {
        this.selectedFiles[fieldName] = file;
        const reader = new FileReader();
        reader.onload = () => this.previewUrls[fieldName] = reader.result;
        reader.readAsDataURL(file);
      }
    }
  }

  onSubmit(): void {
    if (this.profilForm.valid) {
      const formData = new FormData();
      Object.keys(this.profilForm.controls).forEach(key => {
        const value = this.profilForm.get(key)?.value;
        if (value !== undefined && value !== null) {
          formData.append(key, value);
        }
      });

      formData.append('id', this.utilisateurId);

      // Ajouter les fichiers sélectionnés
      Object.keys(this.selectedFiles).forEach(fieldName => {
        formData.append(fieldName, this.selectedFiles[fieldName]);
      });

      this.isLoading = true;
      this.utilisateurService.updateUtilisateur(formData).subscribe({
        next: () => {
          this.isLoading = false;
          alert('Profil mis à jour avec succès');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          this.isLoading = false;
          console.error(err);
          alert('Erreur lors de la mise à jour du profil');
        }
      });
    }
  }


  supprimerCompte() {
    if (confirm('Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.')) {
      this.utilisateurService.deleteUtilisateur(this.utilisateurId).subscribe({
        next: () => {
          alert('Votre compte a été supprimé avec succès.');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          console.error('Erreur de suppression :', err);
          alert("Une erreur s'est produite lors de la suppression du compte.");
        }
      });
    }
  }
 
  onCancel() {
    this.router.navigate(['/frontoffice']);
  }
}
