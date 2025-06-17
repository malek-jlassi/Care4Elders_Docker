package tn.health.productservice.Services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tn.health.productservice.Entities.Produit;
import tn.health.productservice.Repositories.ProduitRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ProduitServiceImpl implements ProduitService{
     ProduitRepository produitRepository;

    @Override
    public Produit ajouterProduit(Produit produit) {
        return produitRepository.save(produit);
    }

    @Override
    public Produit modifierProduit(String id, Produit produit) {
        produit.setId(id);
        return produitRepository.save(produit);
    }

    @Override
    public void supprimerProduit(String id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
        produitRepository.delete(produit);
    }

    @Override
    public Produit getProduitById(String id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
    }

    @Override
    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }




    @Override
    public List<Produit> trierParNom(String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by("nomProduit").descending() : Sort.by("nomProduit").ascending();
        return produitRepository.findAll(sort);
    }

    @Override
    public List<Produit> filtrerParIntervallePrix(Double minPrix, Double maxPrix) {
        return produitRepository.findByPrixBetween(minPrix, maxPrix);
    }
}
