package Project1.com.LibraryManagement.Service;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
@Service
public class FileUpLoadImpl implements FileUpload{
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        return cloudinary.uploader()
                .upload(
                        multipartFile.getBytes(),
                        Map.of("public_id", UUID.randomUUID().toString())
                )
                .get("url")
                .toString();
    }


}
