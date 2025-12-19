package Project1.com.LibraryManagement.Repository;

import Project1.com.LibraryManagement.Entity.PinBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PinBookRepos extends JpaRepository<PinBooks, Long> {
    List<PinBooks> findByUsers_Id(Long userId);
    Optional<PinBooks> findByUsers_IdAndBooks_Id(Long userId, Long bookId);

}
