import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Produit } from '../../models/produitmedical.model';
import { ProduitMedicalService } from '../../services/produitmedical.service';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-produit-medical',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './produit-medical.component.html',
  styleUrls: ['./produit-medical.component.css']
})
export class ProduitMedicalComponent implements OnInit {
  produitForm!: FormGroup;
  produits: Produit[] = [];
  editingProduitId: string | null = null;
  imageProduitFile: File | null = null;
  imageOcrFile: File | null = null;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private produitService: ProduitMedicalService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadProduits();
  }

  initForm(): void {
    this.produitForm = this.fb.group({
      descriptionProduit: ['', Validators.required],
      prix: [0, [Validators.required, Validators.min(0)]],
      qt: [0, [Validators.required, Validators.min(0)]],
       region: ['', Validators.required], // ✅ nouveau champ
    });
  }

  onSubmit(): void {
    console.log('Tentative de soumission...');

    if (this.produitForm.invalid) {
      this.errorMessage = 'Veuillez remplir tous les champs requis.';
      return;
    }

    const formData = new FormData();

    // Ajouter l'image OCR si disponible
    if (this.imageOcrFile) {
      formData.append('ImageOCR', this.imageOcrFile); // ✅ envoyer le fichier OCR
    }

    // Ajouter les autres champs texte
    formData.append('descriptionProduit', this.produitForm.value.descriptionProduit);
    formData.append('prix', this.produitForm.value.prix.toString());
    formData.append('qt', this.produitForm.value.qt.toString());
    formData.append('region', this.produitForm.value.region); // ✅ ajouter la région


    // Ajouter l'image produit normale si disponible
    if (this.imageProduitFile) {
      formData.append('image', this.imageProduitFile);
    }

    // Vérification si c'est une modification ou un ajout
    if (this.editingProduitId) {
      // Modifier un produit
      this.produitService.modifierProduit(this.editingProduitId, formData).subscribe({
        next: () => {
          this.loadProduits(); // Recharge toute la liste avec les nouvelles données OCR
          this.resetForm();
        },
        error: (err) => {
          console.error('Erreur mise à jour produit :', err);
          this.errorMessage = 'Erreur lors de la mise à jour.';
        }
      });
    } else {
      // Pour ajout : image OCR est obligatoire
      if (!this.imageOcrFile) {
        this.errorMessage = 'Veuillez ajouter une image OCR.';
        return;
      }
      formData.append('ImageOCR', this.imageOcrFile);

      // Ajouter un produit avec OCR
      this.produitService.ajouterProduitAvecOCR(formData).subscribe({
        next: (produit) => {
          this.produits.push(produit);
          this.resetForm();
        },
        error: (err) => {
          console.error('Erreur ajout produit :', err);
          this.errorMessage = 'Erreur lors de l\'ajout du produit.';
        }
      });
    }
  }

  loadProduits(): void {
    this.produitService.getAllProduits().subscribe(data => {
      this.produits = data.map(produit => ({
        ...produit,
        statusProduit: produit.qt === 0 ? 'RUPTURE_DE_STOCK' : 'EN_STOCK'
      }));
    });
  }

  deleteProduit(id: string | undefined): void {
    if (id && confirm('Voulez-vous vraiment supprimer ce produit ?')) {
      this.produitService.supprimerProduit(id).subscribe(() => {
        this.produits = this.produits.filter(p => p.id !== id);
      });
    }
  }

  editProduit(produit: Produit): void {
    this.editingProduitId = produit.id ?? null;
    this.produitForm.patchValue({
      descriptionProduit: produit.descriptionProduit,
      prix: produit.prix,
      qt: produit.qt,
      region: produit.region // ✅ lors de la modification
    });
    this.imageProduitFile = null;
    this.imageOcrFile = null;
  }

  resetForm(): void {
    this.produitForm.reset({
      descriptionProduit: '',
      prix: 0,
      qt: 0,
      region: '' // ✅ reset région

    });
    this.editingProduitId = null;
    this.imageProduitFile = null;
    this.imageOcrFile = null;
  }

  onImageProduitSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.imageProduitFile = file;
      console.log('Image produit sélectionnée :', file.name);
    }
  }

  onImageOcrSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.imageOcrFile = file;
      console.log('Image OCR sélectionnée :', file.name);
    }
  }
}
