package Project1.com.LibraryManagement.Repository;

import Project1.com.LibraryManagement.Entity.BorrowRecord;
import Project1.com.LibraryManagement.Entity.BorrowRequest;
import Project1.com.LibraryManagement.Entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BorrowRequestRepos extends JpaRepository<BorrowRequest, Long> {
    Optional<BorrowRequest> findByUsers_IdAndBookCopy_Book_IdAndRequestStatus(
            Long userId,
            Long bookId,
            RequestStatus requestStatus
    );

    boolean existsByUsers_IdAndBookCopy_Book_IdAndRequestStatus(Long userId, Long bookId, RequestStatus requestStatus);
}
