package tn.health.productservice.Services;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OCRService {

    // Méthode principale pour extraire le texte
    public String extractTextFromImage(File imageFile) {
        ITesseract tesseract = new Tesseract();

        // Configure le chemin vers "tessdata" et spécifie plusieurs langues
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        tesseract.setLanguage("fra+eng"); // Charge à la fois français et anglais

        try {
            // Extraction du texte
            String extractedText = tesseract.doOCR(imageFile);

            // Nettoyage du texte extrait
            return cleanExtractedText(extractedText);
        } catch (TesseractException e) {
            e.printStackTrace();
            return "Erreur OCR";
        }
    }

    // Méthode pour nettoyer le texte extrait
    public String cleanExtractedText(String extractedText) {
        // Remplacer tous les caractères non-alphanumériques (sauf les espaces)
        return extractedText.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
    }
}