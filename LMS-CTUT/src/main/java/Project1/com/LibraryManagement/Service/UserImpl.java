package Project1.com.LibraryManagement.Service;

import Project1.com.LibraryManagement.DTO.UserGoogleDTO;
import Project1.com.LibraryManagement.Entity.Users;
import Project1.com.LibraryManagement.Repository.UserRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Service
public class UserImpl implements UserService{
    @Autowired
    public UserRepos usersRepos;

    @Override
    public boolean existsUser(String email) {
        return usersRepos.existsByEmail(email) ;
    }

    @Override
    public Users saveUser(Users users) {
        return usersRepos.save(users);
    }

    @Override
    public Optional<Users> findById(Long id) {
        return usersRepos.findById(id);
    }

    @Override
    public Optional<Users> findByEmail(String email) {
        return usersRepos.findByEmail(email);
    }

    @Override
    public Optional<Users> deleteById(Long Id) {
        return Optional.empty();
    }

    @Override
    public List<Users> getAllUsers() {
        return usersRepos.findAll();
    }

    @Override
    @Transactional
    public void deleteUsers(Long id) {
        Users user = usersRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        usersRepos.delete(user);
    }

    @Override
    @Cacheable(value = "user-page", key = "'page-' + #offset + '-size-' + #pageSize")
    public Page<Users> findUserPagination(int offset, int pageSize) {
        Page<Users> usersPage = usersRepos.findAll(PageRequest.of(offset,pageSize));
        return usersPage;
    }

    @Override
    public Users findByFullNameAndPhoneNumber(String fullName, String phoneNumber) {
        return usersRepos.findByFullNameAndPhoneNumber(fullName, phoneNumber);
    }
}
