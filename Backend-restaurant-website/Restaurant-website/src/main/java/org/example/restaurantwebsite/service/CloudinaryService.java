package org.example.restaurantwebsite.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(@Value("${cloudinary.cloud_name}") String cloudName,
                             @Value("${cloudinary.api_key}") String apiKey,
                             @Value("${cloudinary.api_secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    // Загружаем изображение в Cloudinary и получаем URL
    public String uploadImage(byte[] fileData) throws IOException {
        // Загрузка изображения
        Map uploadResult = cloudinary.uploader().upload(fileData, ObjectUtils.emptyMap());
        return (String) uploadResult.get("url"); // Возвращаем URL обработанного изображения
    }
}
