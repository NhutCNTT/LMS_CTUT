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


    @Query("""
SELECT DISTINCT b
FROM Books b
LEFT JOIN FETCH b.authors a
WHERE
    (:keyword IS NULL OR :keyword = ''
     OR LOWER(b.bookName) LIKE LOWER(CONCAT('%', :keyword, '%'))
     OR LOWER(b.bookCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
     OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))
     OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
AND (:status IS NULL OR :status = '' OR LOWER(b.status) = LOWER(:status))
""")
    Page<Books> searchBooks(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable
    );

    @Query(value = """
    SELECT DISTINCT b.book_name
    FROM books b
    LEFT JOIN book_author ab ON ab.book_id = b.id
    LEFT JOIN author a ON a.author_id = ab.author_id
    WHERE
        (
            MATCH(b.book_name, b.book_code, b.isbn)
                AGAINST(:keyword IN NATURAL LANGUAGE MODE)
            OR MATCH(a.name)
                AGAINST(:keyword IN NATURAL LANGUAGE MODE)
            OR b.book_name LIKE CONCAT(:keyword, '%')
        )
    ORDER BY
        (
            IFNULL(
                MATCH(b.book_name, b.book_code, b.isbn)
                    AGAINST(:keyword IN NATURAL LANGUAGE MODE),
                0
            )
            +
            IFNULL(
                MATCH(a.name)
                    AGAINST(:keyword IN NATURAL LANGUAGE MODE),
                0
            )
        ) DESC
    LIMIT 8
""", nativeQuery = true)
    List<String> suggestKeyword(@Param("keyword") String keyword);

}
