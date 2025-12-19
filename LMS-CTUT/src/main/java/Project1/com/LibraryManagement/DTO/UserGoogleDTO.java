package Project1.com.LibraryManagement.DTO;

import Project1.com.LibraryManagement.Entity.Users;
import jakarta.persistence.JoinColumn;

public class UserGoogleDTO {
    private String email;
    private String fullName;

    public UserGoogleDTO(){

    }

    public UserGoogleDTO(String email, String fullName) {
        this.email = email;
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
