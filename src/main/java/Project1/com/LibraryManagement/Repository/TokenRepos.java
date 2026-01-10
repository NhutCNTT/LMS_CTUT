package Project1.com.LibraryManagement.Repository;

import Project1.com.LibraryManagement.Entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TokenRepos extends JpaRepository<PasswordResetToken,Long> {
    PasswordResetToken findByToken(String token);
    void deleteByExpiredDateTimeBefore(LocalDateTime now);

}
