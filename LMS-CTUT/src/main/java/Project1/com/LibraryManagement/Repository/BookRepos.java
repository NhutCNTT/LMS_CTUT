package Project1.com.LibraryManagement.Repository;

import Project1.com.LibraryManagement.Entity.Books;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface BookRepos extends JpaRepository<Books,Long> {
    Boolean existsByBookCodeAndBookName(String bookCode, String bookName);
    Optional<Books> findById(Long id);
    Books findByBookCodeAndBookName(String bookCode, String bookName);


    @Query(
        value = """
        SELECT * FROM books
        WHERE 
            (:keyword IS NULL OR :keyword = ''
             OR MATCH(book_name, book_code, isbn, author)
                AGAINST(
                    CASE WHEN :keyword IS NULL OR :keyword = '' THEN NULL
                         ELSE CONCAT(:keyword, '*')
                    END
                IN BOOLEAN MODE))
          AND (:status IS NULL OR :status = '' OR LOWER(status) = LOWER(:status))
        """,
        countQuery = """
        SELECT COUNT(*) FROM books
        WHERE 
            (:keyword IS NULL OR :keyword = ''
             OR MATCH(book_name, book_code, isbn, author)
                AGAINST(
                    CASE WHEN :keyword IS NULL OR :keyword = '' THEN NULL
                         ELSE CONCAT(:keyword, '*')
                    END
                IN BOOLEAN MODE))
          AND (:status IS NULL OR :status = '' OR LOWER(status) = LOWER(:status))
        """,
        nativeQuery = true
)
Page<Books> searchBooks(@Param("keyword") String keyword,
                        @Param("status") String status,
                        Pageable pageable);


    @Query(value = """
        SELECT book_name
        FROM books
        WHERE 
            (MATCH(book_name, author, isbn, book_code) AGAINST(:keyword IN NATURAL LANGUAGE MODE))
            OR book_name LIKE CONCAT(:keyword, '%')
        GROUP BY book_name
        ORDER BY MATCH(book_name, author, isbn, book_code) AGAINST(:keyword IN NATURAL LANGUAGE MODE) DESC
        LIMIT 8
    """, nativeQuery = true)
    List<String> suggestKeyword(@Param("keyword") String keyword);

}
