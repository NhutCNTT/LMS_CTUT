package Project1.com.LibraryManagement.Repository;

import Project1.com.LibraryManagement.Entity.BookCopy;
import Project1.com.LibraryManagement.Entity.BookCopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookCopyRepos extends JpaRepository<BookCopy, Integer> {
    Optional<BookCopy> findFirstByBook_IdAndStatusOrderByIdAsc(
            Long bookId,
            BookCopyStatus status
    );
}
