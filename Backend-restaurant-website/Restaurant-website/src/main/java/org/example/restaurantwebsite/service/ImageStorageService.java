//package org.example.restaurantwebsite.service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@Service
//public class ImageStorageService {
//
//    @Value("${image.upload-dir}")
//    private String uploadDir;  // Путь к директории для хранения изображений
//
//    // Метод для сохранения изображения
//    public String storeImage(MultipartFile file) throws IOException {
//        // Создаём директорию, если она не существует
//        Path uploadPath = Paths.get(uploadDir);
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//
//        // Определяем путь для сохранения файла
//        Path filePath = uploadPath.resolve(file.getOriginalFilename());
//
//        // Сохраняем файл
//        file.transferTo(filePath.toFile());
//
//        // Возвращаем путь к файлу (относительный путь к изображению)
//        return filePath.toString();
//    }
//}
