package tn.health.productservice.RestControllers;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.health.productservice.Entities.Produit;
import tn.health.productservice.Repositories.ProduitRepository;
import tn.health.productservice.Services.OCRService;
import tn.health.productservice.Services.ProduitService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

//@CrossOrigin(origins = "http://localhost:4200", originPatterns = "http://localhost:4300")
@RestController
@RequestMapping("/produits")
@RequiredArgsConstructor
public class ProduitController {
    private final ProduitService produitService;
   // ProduitRepository produitRepository;
    private final ProduitRepository produitRepository;
    private final OCRService ocrService;


    @PostMapping("/ajouterProduitAvecOCR")
    public ResponseEntity<Produit> ajouterProduitAvecOCR(
            @RequestParam("ImageOCR") MultipartFile ImageOCR,
            @RequestParam("descriptionProduit") String descriptionProduit,
            @RequestParam("prix") Double prix,
            @RequestParam("qt") Integer qt,
            @RequestParam("region") String region,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        // Sauvegarder temporairement l'image OCR sur le disque
        String tempDir = System.getProperty("java.io.tmpdir");
        String ocrImageName = UUID.randomUUID() + "_" + ImageOCR.getOriginalFilename();
        Path ocrImagePath = Paths.get(tempDir, ocrImageName);
        Files.copy(ImageOCR.getInputStream(), ocrImagePath, StandardCopyOption.REPLACE_EXISTING);

        // Extraire le texte de l'image (nom du produit)
        String ocrText = ocrService.extractTextFromImage(ocrImagePath.toFile());
        String nomProduit = ocrText.split("\n")[0].trim(); // Prend la première ligne comme nom

        // Préparer le produit
        Produit produit = new Produit();
        produit.setNomProduit(nomProduit);
        produit.setDescriptionProduit(descriptionProduit);
        produit.setPrix(prix);
        produit.setQt(qt);
        produit.setRegion(region);

        // Upload image du produit (optionnelle)
        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get("uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            produit.setImage("/uploads/" + fileName);
        }

        // Enregistrer
        Produit savedProduit = produitService.ajouterProduit(produit);
        return ResponseEntity.ok(savedProduit);
    }
    @PutMapping("/modifierProduit/{id}")
    public ResponseEntity<Produit> modifierProduit(
            @PathVariable String id,
            @RequestParam(value = "ImageOCR", required = false) MultipartFile ImageOCR,
            @RequestParam(value = "descriptionProduit", required = false) String descriptionProduit,
            @RequestParam(value = "prix", required = false) Double prix,
            @RequestParam(value = "qt", required = false) Integer qt,
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        Produit existing = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        // Si une nouvelle image OCR est fournie, extraire un nouveau nom ET remplacer l'image du produit
        if (ImageOCR != null && !ImageOCR.isEmpty()) {
            String tempDir = System.getProperty("java.io.tmpdir");
            String ocrImageName = UUID.randomUUID() + "_" + ImageOCR.getOriginalFilename();
            Path ocrImagePath = Paths.get(tempDir, ocrImageName);
            Files.copy(ImageOCR.getInputStream(), ocrImagePath, StandardCopyOption.REPLACE_EXISTING);

            String ocrText = ocrService.extractTextFromImage(ocrImagePath.toFile());
            String nomProduit = ocrText.split("\n")[0].trim();
            existing.setNomProduit(nomProduit);

            // Enregistrer l'image OCR comme nouvelle image du produit
            String fileName = UUID.randomUUID() + "_" + ImageOCR.getOriginalFilename();
            Path uploadPath = Paths.get("uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(ImageOCR.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            existing.setImage("/uploads/" + fileName);
        }

        // Mise à jour des autres champs si présents
        if (descriptionProduit != null) {
            existing.setDescriptionProduit(descriptionProduit);
        }
        if (prix != null && prix != 0.0) {
            existing.setPrix(prix);
        }
        if (qt != null) {
            existing.setQt(qt);
        }
        if (region != null) {
            existing.setRegion(region);
        }
        // Si une image standard est envoyée (priorité si nomImageOCR n’est pas présent)
        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get("uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            existing.setImage("/uploads/" + fileName);
        }

        return ResponseEntity.ok(produitRepository.save(existing));
    }

    @GetMapping("/ListerTousProduits")
    public ResponseEntity<List<Produit>> getAllProduits() {
        return ResponseEntity.ok(produitService.getAllProduits());
    }

    @GetMapping("/ListerProduit/{id}")
    public ResponseEntity<Produit> getProduit(@PathVariable String id) {
        return ResponseEntity.ok(produitService.getProduitById(id));
    }

    @DeleteMapping("/supprimerProduit/{id}")
    public ResponseEntity<Void> supprimerProduit(@PathVariable String id) {
        produitService.supprimerProduit(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/filtrerParPrix")
    public ResponseEntity<List<Produit>> filtrerParIntervallePrix(
            @RequestParam("minPrix") Double minPrix,
            @RequestParam("maxPrix") Double maxPrix) {
        return ResponseEntity.ok(produitService.filtrerParIntervallePrix(minPrix, maxPrix));
    }
    @GetMapping("/trierParNomm")
    public ResponseEntity<List<Produit>> trierParNom(
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(produitService.trierParNom(direction));
    }
}
