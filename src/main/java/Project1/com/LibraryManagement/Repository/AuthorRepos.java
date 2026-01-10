package Project1.com.LibraryManagement.Repository;

import Project1.com.LibraryManagement.Entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepos extends JpaRepository<Author, Integer> {
    Author findByName(String name);
}
