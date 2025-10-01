package com.anshul.hotel.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class ImageUploadService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file,String folderName){
try{
Map uploadResult = cloudinary.uploader().upload(
        file.getBytes(),
        ObjectUtils.asMap("folder",folderName)
);
return (String) uploadResult.get("secure_url");
}catch (Exception e){
    throw new RuntimeException("Image upload failed", e);
}
    }
    public void deleteImage(String imageUrl){
try{
   String publicId = extractPublicId(imageUrl);
   cloudinary.uploader().destroy(publicId,ObjectUtils.emptyMap());
}catch (Exception e){
    throw new RuntimeException("Failed to delete image: " + e.getMessage());
}
    }
    private String extractPublicId(String url) {
        String[] parts = url.split("/");
        String fileName = parts[parts.length - 1].split("\\.")[0];

        // Find "hotels" index
        int hotelsIndex = -1;
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("hotels")) {
                hotelsIndex = i;
                break;
            }
        }

        if (hotelsIndex == -1) {
            throw new RuntimeException("Invalid URL: hotels folder not found");
        }

        // PublicId = everything from "hotels" till fileName
        StringBuilder publicId = new StringBuilder();
        for (int i = hotelsIndex; i < parts.length - 1; i++) {
            publicId.append(parts[i]).append("/");
        }
        publicId.append(fileName);

        return publicId.toString();
    }

}
