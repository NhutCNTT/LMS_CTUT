package Project1.com.LibraryManagement.DTO;

public class BorrowingDTO {
    public Long userId;
    public Long bookId;
    public Long requestId;


    public BorrowingDTO(){

    }

    public BorrowingDTO(Long bookId, Long requestId, Long userId) {
        this.bookId = bookId;
        this.requestId = requestId;
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
