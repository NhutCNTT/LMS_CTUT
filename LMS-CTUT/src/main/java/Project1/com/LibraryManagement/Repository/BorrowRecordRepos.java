package Project1.com.LibraryManagement.Repository;

import Project1.com.LibraryManagement.DTO.BorrowRecordResponeDTO;
import Project1.com.LibraryManagement.Entity.BorrowRecord;
import Project1.com.LibraryManagement.Entity.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BorrowRecordRepos extends JpaRepository<BorrowRecord, Long> {
    Long countByUsersId(Long userId);
    Long countByUsersIdAndStatus(Long userId, RecordStatus status);
    List<BorrowRecord> findByUsersId(Long userId);
    List<BorrowRecord> findByStatus(RecordStatus status);
    Optional<BorrowRecord> findByUsersIdAndBooksIdAndStatus(
            Long userId,
            Long bookId,
            RecordStatus status
    );


    @Query(
            value = """
        SELECT br 
        FROM BorrowRecord br
        JOIN FETCH br.users u
        JOIN FETCH br.books bo
        WHERE 
            (:keyword IS NULL OR :keyword = '' 
             OR u.fullName LIKE CONCAT('%', :keyword, '%')
             OR u.email LIKE CONCAT('%', :keyword, '%')
             OR u.phoneNumber LIKE CONCAT('%', :keyword, '%')
             OR bo.bookCode LIKE CONCAT('%', :keyword, '%')
             OR bo.bookName LIKE CONCAT('%', :keyword, '%')
             OR CAST(br.id AS string) LIKE CONCAT('%', :keyword, '%')   
            )
        """,
            countQuery = """
        SELECT COUNT(br)
        FROM BorrowRecord br
        JOIN br.users u
        JOIN br.books bo
        WHERE 
            (:keyword IS NULL OR :keyword = '' 
             OR u.fullName LIKE CONCAT('%', :keyword, '%')
             OR u.email LIKE CONCAT('%', :keyword, '%')
             OR u.phoneNumber LIKE CONCAT('%', :keyword, '%')
             OR bo.bookCode LIKE CONCAT('%', :keyword, '%')
             OR bo.bookName LIKE CONCAT('%', :keyword, '%')
             OR CAST(br.id AS string) LIKE CONCAT('%', :keyword, '%')   
            )
        """
    )
    Page<BorrowRecord> searchBorrowRecord(
            @Param("keyword") String keyword,
            Pageable pageable
    );

}

