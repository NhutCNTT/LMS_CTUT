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
    public UserRepos userRepos;
    @Autowired
    public BookRepos bookRepos;
    @Autowired
    public BorrowingRepos borrowingRepos;
    @Autowired
    public BorrowRequestRepos borrowRequestRepos;



    @Override
    @Transactional
  public Borrowing createBorrowing(BorrowingDTO borrowingDTO) {
        Users users = userRepos.findById(borrowingDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Books books = bookRepos.findById(borrowingDTO.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        BorrowRequest borrowRequest = borrowRequestRepos.findById(borrowingDTO.getRequestId())
                .orElseThrow(() -> new RuntimeException("Borrow request not found"));
        Borrowing borrowing = new Borrowing();
        borrowing.setUsers(users);
        borrowing.setBooks(books);
        LocalDateTime now = LocalDateTime.now();
        borrowing.setCreateAt(now);
        borrowing.setDeadline(now.plusDays(1));
        borrowingRepos.save(borrowing);
        borrowRequestRepos.delete(borrowRequest);
        return borrowing;
    }
    @Override
    public void autoDeleteExpiredBorrowing() {
        LocalDateTime now = LocalDateTime.now();
        List<Borrowing> expiredList = borrowingRepos.findByDeadlineBefore(now);

        for (Borrowing borrowing : expiredList) {
            Books book = borrowing.getBooks();
            book.setQuantity(book.getQuantity() + 1);
            bookRepos.save(book);
            borrowingRepos.delete(borrowing);
        }
    }
    @Scheduled(fixedRate = 86400000)
    public void runAutoDeleteScheduler() {
        autoDeleteExpiredBorrowing();
    }


    public List<Borrowing> getAllBorrowing() {
    return borrowingRepos.findAll();
}

}
