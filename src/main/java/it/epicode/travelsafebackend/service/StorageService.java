package it.epicode.travelsafebackend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final Cloudinary cloudinary;

    public String storeFile(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(

                        "resource_type", "auto"
                ));
        return (String) uploadResult.get("secure_url"); // URL pubblico dell'immagine
    }
}
