import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProduitMedicalService } from '../../../services/produitmedical.service';
import { Produit, getStatusProduit } from '../../../models/produitmedical.model';
import { TopbarComponent } from '../../../shared/layout/topbar/topbar.component';
import { NavbarComponent } from '../../../shared/layout/navbar/navbar.component';
import { FooterComponent } from '../../../shared/layout/footer/footer.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-afficher-liste-produit',
  standalone: true,
  imports: [CommonModule, TopbarComponent,
    NavbarComponent,
    FooterComponent],
  templateUrl: './afficher-liste-produit.component.html',
  styleUrl: './afficher-liste-produit.component.css'
})
export class AfficherListeProduitComponent {
  products: Produit[] = [];

  constructor(
    private produitService: ProduitMedicalService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.produitService.getAllProduits().subscribe(data => {
      this.products = data;
    });
  }

  getStatusProduit(product: Produit): string {
    return getStatusProduit(product);
  }

  formatPrice(prix: number): string {
    return prix.toFixed(3) + ' TND';
  }

  goToDelivery(product: Produit) {
    // Navigate to delivery bill screen with product info
    this.router.navigate(['/delivery/bill'], {
      queryParams: {
        productId: product.id,
        nomProduit: product.nomProduit,
        descriptionProduit: product.descriptionProduit,
        region: product.region,
        prix: product.prix,
        qt: product.qt,
        image: product.image
      }
    });
  }
}