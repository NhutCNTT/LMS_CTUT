package Project1.com.LibraryManagement.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import Project1.com.LibraryManagement.Entity.Unit;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnitRepos extends JpaRepository<Unit, Long> {
    Optional<Unit> findById(Long id);
    Optional<Unit> findByName(String name);
}
