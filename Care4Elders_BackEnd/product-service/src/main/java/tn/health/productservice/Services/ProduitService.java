package tn.health.productservice.Services;

import tn.health.productservice.Entities.Produit;

import java.util.List;


public interface ProduitService {
    Produit ajouterProduit(Produit produit);
    Produit modifierProduit(String id, Produit produit);
    void supprimerProduit(String id);
    Produit getProduitById(String id);
    List<Produit> getAllProduits();
    public List<Produit> trierParNom(String direction) ;
    public List<Produit> filtrerParIntervallePrix(Double minPrix, Double maxPrix);
}
