package com.example.gymbackend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(@Value("${cloudinary.url}") String cloudinaryUrl) {
        this.cloudinary = new Cloudinary(cloudinaryUrl);
    }

    public String uploadImage(MultipartFile file) throws IOException {
        @SuppressWarnings("rawtypes")
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "transformation", "w_800,c_limit,q_auto"
        ));
        return uploadResult.get("secure_url").toString();
    }

    public void deleteImage(String imageUrl) {
        try {
            // Extract public ID from Cloudinary URL
            // Format: https://res.cloudinary.com/cloud_name/image/upload/v1234567890/public_id.jpg
            String[] parts = imageUrl.split("/");
            String lastPart = parts[parts.length - 1];
            String publicId = lastPart.substring(0, lastPart.lastIndexOf("."));
            
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            System.err.println("Failed to delete Cloudinary image: " + e.getMessage());
        }
    }
}
