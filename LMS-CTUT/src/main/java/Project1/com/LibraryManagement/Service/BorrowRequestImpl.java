package Project1.com.LibraryManagement.Service;

import Project1.com.LibraryManagement.DTO.BorrowRequestDTO;
import Project1.com.LibraryManagement.Entity.*;
import Project1.com.LibraryManagement.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BorrowRequestImpl implements BorrowRequestService {
    @Autowired
    public UserRepos userRepos;
    @Autowired
    public BookRepos bookRepos;
    @Autowired
    public BorrowRequestRepos borrowRequestRepos;
    @Autowired
    public BorrowRecordRepos borrowRecordRepos;
    @Autowired
    public BorrowingRepos borrowingRepos;

    @Override
    public BorrowRequest createRequest(BorrowRequestDTO borrowRequestDTO) {

        Users users = userRepos.findById(borrowRequestDTO.getUserId())
                .orElseThrow(()-> new RuntimeException("User not found"));
        Books books = bookRepos.findById(borrowRequestDTO.getBookId())
                .orElseThrow(()-> new RuntimeException("Book not found"));

        boolean isBorrowing = borrowRecordRepos
                .findByUsersIdAndBooksIdAndStatus(
                        borrowRequestDTO.getUserId(),
                        borrowRequestDTO.getBookId(),
                        RecordStatus.BORROWED
                )
                .isPresent();

        if (isBorrowing) {
            throw new RuntimeException("Bạn đang mượn cuốn này rồi. Không thể đặt thêm!");
        }

        boolean hasPendingRequest = borrowRequestRepos
                .findByUsersIdAndBooksIdAndRequestStatus(
                        borrowRequestDTO.getUserId(),
                        borrowRequestDTO.getBookId(),
                        RequestStatus.PENDING
                )
                .isPresent();

        if (hasPendingRequest) {
            throw new RuntimeException("Bạn đã đặt cuốn này rồi. Vui lòng chờ thủ thư duyệt!");
        }

        boolean existsInBorrowing = borrowingRepos.existsByUsers_IdAndBooks_Id(
                borrowRequestDTO.getUserId(),
                borrowRequestDTO.getBookId()
        );

        if (existsInBorrowing) {
            throw new RuntimeException("Bạn đã được duyệt cuốn này hoặc đang chờ lấy. Không thể đặt thêm!");
        }


        if (books.getQuantity() <= 0) {
            throw new RuntimeException("Sách đã hết, không thể đặt mượn!");
        }
        books.setQuantity(books.getQuantity() - 1);
        bookRepos.save(books);
        BorrowRequest borrowRequest = new BorrowRequest();
        borrowRequest.setUsers(users);
        borrowRequest.setBooks(books);
        borrowRequest.setRequestStatus(RequestStatus.PENDING);
        return borrowRequestRepos.save(borrowRequest);

    }

        @Override
    public List<BorrowRequest> getAllRequest() {
        return borrowRequestRepos.findAll();
    }

    @Override
    public Long deleteRequest(Long id) {
        borrowRequestRepos.deleteById(id);
        return id;
    }


}
