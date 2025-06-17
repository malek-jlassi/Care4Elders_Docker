package tn.health.productservice.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.health.productservice.Entities.Produit;

import java.util.List;

@Repository
public interface ProduitRepository extends MongoRepository<Produit, String> {
    List<Produit> findByPrixBetween(Double minPrix, Double maxPrix);
}
