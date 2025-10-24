package com.example.library_management.common.util;

import com.example.library_management.exceptions.client.InvalidFileTypeException;
import com.example.library_management.exceptions.client.InvalidInputException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;

@Service
public class ImageUploadService {

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif");

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUploadService.class);

    // application.properties dosyasından 'file.upload-dir' değerini bu değişkene ata.
    @Value("${file.upload-dir}")
    private String uploadPath;

    private Path rootLocation;

    /**
     * Bu metot, servis oluşturulduktan hemen sonra çalışır (@PostConstruct sayesinde).
     * Amacı, resimlerin yükleneceği klasörün var olup olmadığını kontrol etmek ve
     * yoksa oluşturmaktır.
     */
    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(uploadPath);
        try {
            Files.createDirectories(rootLocation);
            LOGGER.info("Yükleme klasörü başarıyla oluşturuldu veya zaten mevcut: {}", rootLocation.toAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("Yükleme klasörü oluşturulamadı.", e);
            throw new RuntimeException("Yükleme klasörü oluşturulamadı.", e);
        }
    }

    /**
     * Gelen MultipartFile'ı alır, benzersiz bir isimle kaydeder ve erişim yolunu döndürür.
     *
     * @param file Yüklenecek resim dosyası.
     * @return Dosyanın sunulacağı URL yolu.
     */
    public String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            LOGGER.warn("Kaydedilecek dosya boş veya null.");
            return null;
        }

        try {
            String contentType = file.getContentType();
            if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
                throw new InvalidFileTypeException("Invalid file type : "+ contentType+" It must be jpeg/png/gif");
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID()+ fileExtension;

            Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFilename))
                    .normalize().toAbsolutePath();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            LOGGER.info("{} dosyası başarıyla kaydedildi.", uniqueFilename);

            // 4. ADIM: Erişilebilir URL yolunu döndür.
            // Bu yol, dosyaları sunacak olan bir Resource Handler tarafından kullanılacaktır.
            // Örn: "/media/images/benzersiz-dosya-adi.jpg"
            return "/media/images/" + uniqueFilename;

        } catch (IOException e) {
            LOGGER.error("{} dosyası kaydedilirken bir hata oluştu.", file.getOriginalFilename(), e);
            throw new RuntimeException("Dosya kaydedilirken bir hata oluştu: " + file.getOriginalFilename(), e);
        }
    }
}
