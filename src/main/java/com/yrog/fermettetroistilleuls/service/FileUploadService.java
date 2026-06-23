package com.yrog.fermettetroistilleuls.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * Service gérant l'upload de fichiers images
 * pour les photos des gîtes.
 * Valide le type et la taille avant de sauvegarder.
 */
@Service
public class FileUploadService {

    private static final Logger log = LoggerFactory.getLogger(FileUploadService.class);

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB

    @Value("${upload.dir}")
    private String uploadDir;

    /**
     * Sauvegarde un fichier image uploadé pour un gîte.
     * Vérifie le type MIME et la taille avant sauvegarde.
     * Le fichier est renommé avec un UUID pour éviter
     * les conflits de noms.
     *
     * @param file le fichier uploadé
     * @return l'URL publique de la photo
     * @throws IllegalArgumentException si le fichier est invalide
     * @throws IOException si la sauvegarde échoue
     */
    public String saveGitePhoto(MultipartFile file) throws IOException {

        // Vérification type MIME
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Type de fichier non autorisé. Formats acceptés : JPG, PNG, WEBP, GIF");
        }

        // Vérification taille
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException(
                    "Fichier trop volumineux. Taille maximale : 10MB");
        }

        // Création du dossier si nécessaire
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Génération d'un nom unique
        String extension = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + extension;

        // Sauvegarde du fichier
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        log.info("Photo uploadée : {}", filename);

        // Retourne l'URL publique
        return "/uploads/gites/" + filename;
    }

    /**
     * Extrait l'extension d'un nom de fichier.
     */
    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    /**
     * Sauvegarde un document PDF uploadé.
     * Toujours sauvegardé sous le même nom pour
     * que le lien public ne change jamais.
     *
     * @param file le fichier PDF uploadé
     * @return l'URL publique du document
     * @throws IllegalArgumentException si le fichier n'est pas un PDF
     * @throws IOException si la sauvegarde échoue
     */
    public String saveTarifsPdf(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fichier vide");
        }

        String contentType = file.getContentType();
        if (!"application/pdf".equals(contentType)) {
            throw new IllegalArgumentException(
                    "Format non autorisé. Seuls les PDF sont acceptés.");
        }

        // Créer le dossier si nécessaire
        Path uploadPath = Paths.get(uploadDir).getParent().resolve("documents");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Toujours le même nom → lien public permanent
        Path filePath = uploadPath.resolve("tarifs.pdf");
        Files.copy(file.getInputStream(), filePath,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        log.info("Grille tarifaire mise à jour");
        return "/uploads/documents/tarifs.pdf";
    }

}
