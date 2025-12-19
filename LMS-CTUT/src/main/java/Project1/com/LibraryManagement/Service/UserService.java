package Project1.com.LibraryManagement.Service;

import Project1.com.LibraryManagement.DTO.UserGoogleDTO;
import Project1.com.LibraryManagement.Entity.Users;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface UserService {
    public boolean existsUser(String email);
    public Users saveUser(Users users);
    public Optional<Users> findById(Long Id);
    public Optional<Users> findByEmail(String email);
    public Optional<Users> deleteById(Long Id);
    public List<Users> getAllUsers();
    public void deleteUsers(Long id);
    public Page<Users> findUserPagination(int offset, int pageSize);
    Users findByFullNameAndPhoneNumber(String fullName, String phoneNumber);



}
