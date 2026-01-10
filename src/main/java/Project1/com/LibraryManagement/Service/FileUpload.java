package Project1.com.LibraryManagement.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUpload {
    public String uploadFile(MultipartFile multipartFile) throws IOException;
}
