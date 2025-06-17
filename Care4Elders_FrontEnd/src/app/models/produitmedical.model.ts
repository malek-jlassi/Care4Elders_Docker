// src/app/models/Produit.model.ts

export interface Produit {
  id?: string;
  nomProduit: string;
  descriptionProduit: string;
  prix: number;
  qt: number;
  region: string;
  image?: string; // URL ou nom du fichier image
}

// Statut calculé dynamiquement
export function getStatusProduit(produit: Produit): 'EN_STOCK' | 'RUPTURE_DE_STOCK' {
  return produit.qt && produit.qt > 0 ? 'EN_STOCK' : 'RUPTURE_DE_STOCK';
}
