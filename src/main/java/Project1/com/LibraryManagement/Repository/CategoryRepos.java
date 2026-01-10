package Project1.com.LibraryManagement.Repository;

import Project1.com.LibraryManagement.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepos extends JpaRepository<Category, Integer> {
    Category findByName(String name);
}
