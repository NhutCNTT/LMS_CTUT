package Project1.com.LibraryManagement.Repository;

import Project1.com.LibraryManagement.Entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRepos extends JpaRepository<Borrowing, Long> {
    List<Borrowing> findByDeadlineBefore(LocalDateTime now);
    Optional<Borrowing> findByUsersIdAndBooksIdAndBorrowingStatus(
            Long userId,
            Long bookId,
            BorrowingStatus borrowingStatus
    );
    boolean existsByUsers_IdAndBooks_Id(Long userId, Long bookId);


    @Query(
            value = """
        SELECT b 
        FROM Borrowing b
        JOIN FETCH b.users u
        JOIN FETCH b.books bo
        WHERE 
            (:keyword IS NULL OR :keyword = '' 
             OR u.fullName LIKE CONCAT('%', :keyword, '%')
             OR u.email LIKE CONCAT('%', :keyword, '%')
             OR u.phoneNumber LIKE CONCAT('%', :keyword, '%')
             OR bo.bookCode LIKE CONCAT('%', :keyword, '%')
             OR bo.bookName LIKE CONCAT('%', :keyword, '%')
            )
        """,
            countQuery = """
        SELECT COUNT(b)
        FROM Borrowing b
        JOIN b.users u
        JOIN b.books bo
        WHERE 
            (:keyword IS NULL OR :keyword = '' 
             OR u.fullName LIKE CONCAT('%', :keyword, '%')
             OR u.email LIKE CONCAT('%', :keyword, '%')
             OR u.phoneNumber LIKE CONCAT('%', :keyword, '%')
             OR bo.bookCode LIKE CONCAT('%', :keyword, '%')
             OR bo.bookName LIKE CONCAT('%', :keyword, '%')
            )
        """
    )
    Page<Borrowing> searchBorrowing(
            @Param("keyword") String keyword,
            Pageable pageable
    );




}
