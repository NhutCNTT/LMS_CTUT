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
    private BookCopyRepos bookCopyRepository;
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
                .existsByUsers_IdAndBookCopy_Book_IdAndStatus(
                        borrowRequestDTO.getUserId(),
                        borrowRequestDTO.getBookId(),
                        RecordStatus.BORROWED
                );

        if (isBorrowing) {
            throw new RuntimeException("Bạn đang mượn cuốn này rồi. Không thể đặt thêm!");
        }

        boolean hasPendingRequest = borrowRequestRepos
                .existsByUsers_IdAndBookCopy_Book_IdAndRequestStatus(
                        borrowRequestDTO.getUserId(),
                        borrowRequestDTO.getBookId(),
                        RequestStatus.PENDING
                );

        if (hasPendingRequest) {
            throw new RuntimeException("Bạn đã đặt cuốn này rồi. Vui lòng chờ thủ thư duyệt!");
        }

        boolean existsInBorrowing = borrowingRepos
                .existsByUsers_IdAndBookCopy_Book_Id(
                        borrowRequestDTO.getUserId(),
                        borrowRequestDTO.getBookId()
                );


        if (existsInBorrowing) {
            throw new RuntimeException("Bạn đã được duyệt cuốn này hoặc đang chờ lấy. Không thể đặt thêm!");
        }


        BookCopy bookCopy = bookCopyRepository
                .findFirstByBook_IdAndStatusOrderByIdAsc(
                        borrowRequestDTO.getBookId(),
                        BookCopyStatus.AVAILABLE
                )
                .orElseThrow(() ->
                        new RuntimeException("Sách đã hết, không thể đặt mượn!")
                );

        // 7. Giữ chỗ cuốn sách
        bookCopy.setStatus(BookCopyStatus.BORROWED);
        bookCopyRepository.save(bookCopy);

        // 8. Tạo request
        BorrowRequest borrowRequest = new BorrowRequest();
        borrowRequest.setUsers(users);
        borrowRequest.setBookCopy(bookCopy);
        borrowRequest.setRequestStatus(RequestStatus.PENDING);
        borrowRequest.setCreatedAt(LocalDateTime.now());


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
