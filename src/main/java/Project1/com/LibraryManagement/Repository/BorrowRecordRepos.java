package Project1.com.LibraryManagement.Repository;

import Project1.com.LibraryManagement.Entity.BorrowRecord;
import Project1.com.LibraryManagement.Entity.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BorrowRecordRepos extends JpaRepository<BorrowRecord, Long> {

    Long countByUsers_Id(Long userId);

    Long countByUsers_IdAndStatus(Long userId, RecordStatus status);

    List<BorrowRecord> findByUsers_Id(Long userId);

    List<BorrowRecord> findByStatus(RecordStatus status);


    boolean existsByUsers_IdAndBookCopy_Book_IdAndStatus(
            Long userId,
            Long bookId,
            RecordStatus status
    );

    /* ================= SEARCH ================= */

    @Query(
            value = """
        SELECT br
        FROM BorrowRecord br
        JOIN FETCH br.users u
        JOIN FETCH br.bookCopy bc
        JOIN FETCH bc.book b
        WHERE 
            (:keyword IS NULL OR :keyword = '' 
             OR u.fullName LIKE CONCAT('%', :keyword, '%')
             OR u.email LIKE CONCAT('%', :keyword, '%')
             OR u.phoneNumber LIKE CONCAT('%', :keyword, '%')
             OR b.bookCode LIKE CONCAT('%', :keyword, '%')
             OR b.bookName LIKE CONCAT('%', :keyword, '%')
             OR CAST(br.id AS string) LIKE CONCAT('%', :keyword, '%')
            )
        """,
            countQuery = """
        SELECT COUNT(br)
        FROM BorrowRecord br
        JOIN br.users u
        JOIN br.bookCopy bc
        JOIN bc.book b
        WHERE 
            (:keyword IS NULL OR :keyword = '' 
             OR u.fullName LIKE CONCAT('%', :keyword, '%')
             OR u.email LIKE CONCAT('%', :keyword, '%')
             OR u.phoneNumber LIKE CONCAT('%', :keyword, '%')
             OR b.bookCode LIKE CONCAT('%', :keyword, '%')
             OR b.bookName LIKE CONCAT('%', :keyword, '%')
             OR CAST(br.id AS string) LIKE CONCAT('%', :keyword, '%')
            )
        """
    )
    Page<BorrowRecord> searchBorrowRecord(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
