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

    // Các lượt mượn quá hạn
    List<Borrowing> findByDeadlineBefore(LocalDateTime now);

    // 1 user đang mượn 1 bookCopy cụ thể
    Optional<Borrowing> findByUsers_IdAndBookCopy_IdAndBorrowingStatus(
            Long userId,
            Long bookCopyId,
            BorrowingStatus borrowingStatus
    );

    // Kiểm tra user đã mượn sách (theo book) chưa
    boolean existsByUsers_IdAndBookCopy_Book_Id(Long userId, Long bookId);

    // Tìm kiếm Borrowing theo keyword (tên, email, phone hoặc mã/tên sách) — dùng pageable
    @Query("""
        SELECT DISTINCT b
        FROM Borrowing b
        JOIN b.users u
        JOIN b.bookCopy bc
        JOIN bc.book bo
        WHERE
            (:keyword IS NULL OR :keyword = ''
             OR u.fullName LIKE CONCAT('%', :keyword, '%')
             OR u.email LIKE CONCAT('%', :keyword, '%')
             OR u.phoneNumber LIKE CONCAT('%', :keyword, '%')
             OR bo.bookCode LIKE CONCAT('%', :keyword, '%')
             OR bo.bookName LIKE CONCAT('%', :keyword, '%')
            )
    """)
    Page<Borrowing> searchBorrowing(@Param("keyword") String keyword, Pageable pageable);
}
