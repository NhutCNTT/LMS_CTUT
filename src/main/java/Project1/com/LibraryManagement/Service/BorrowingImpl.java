package Project1.com.LibraryManagement.Service;

import Project1.com.LibraryManagement.DTO.BorrowRecordDTO;
import Project1.com.LibraryManagement.DTO.BorrowRecordResponeDTO;
import Project1.com.LibraryManagement.DTO.BorrowingDTO;
import Project1.com.LibraryManagement.DTO.BorrowingResponseDTO;
import Project1.com.LibraryManagement.Entity.*;
import Project1.com.LibraryManagement.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowingImpl implements BorrowingService {
    @Autowired
    private UserRepos userRepos;

    @Autowired
    private BorrowingRepos borrowingRepos;

    @Autowired
    private BorrowRequestRepos borrowRequestRepos;

    @Autowired
    private BookCopyRepos bookCopyRepos;

    @Override
    public Borrowing createBorrowing(BorrowingDTO borrowingDTO) {

        Users users = userRepos.findById(borrowingDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        BorrowRequest borrowRequest = borrowRequestRepos.findById(borrowingDTO.getRequestId())
                .orElseThrow(() -> new RuntimeException("Borrow request not found"));

        // LẤY CUỐN SÁCH TỪ REQUEST
        BookCopy bookCopy = borrowRequest.getBookCopy();

        // ĐỔI TRẠNG THÁI → ĐÃ MƯỢN
        bookCopy.setStatus(BookCopyStatus.BORROWED);
        bookCopyRepos.save(bookCopy);

        // TẠO BORROWING
        Borrowing borrowing = new Borrowing();
        borrowing.setUsers(users);
        borrowing.setBookCopy(bookCopy);

        LocalDateTime now = LocalDateTime.now();
        borrowing.setCreatedAt(now);
        borrowing.setDeadline(now.plusDays(1)); // hoặc số ngày cấu hình

        borrowingRepos.save(borrowing);

        // XÓA REQUEST SAU KHI DUYỆT
        borrowRequestRepos.delete(borrowRequest);

        return borrowing;
    }

    @Override
    public void autoDeleteExpiredBorrowing() {

        LocalDateTime now = LocalDateTime.now();
        List<Borrowing> expiredList = borrowingRepos.findByDeadlineBefore(now);

        for (Borrowing borrowing : expiredList) {

            BookCopy bookCopy = borrowing.getBookCopy();

            // TRẢ SÁCH VỀ KỆ
            bookCopy.setStatus(BookCopyStatus.AVAILABLE);
            bookCopyRepos.save(bookCopy);

            borrowingRepos.delete(borrowing);
        }
    }

    @Scheduled(fixedRate = 86400000) // mỗi ngày
    public void runAutoDeleteScheduler() {
        autoDeleteExpiredBorrowing();
    }

    @Override
    public List<Borrowing> getAllBorrowing() {
        return borrowingRepos.findAll();
    }

}
