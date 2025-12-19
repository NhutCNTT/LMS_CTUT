package Project1.com.LibraryManagement.DTO;

import Project1.com.LibraryManagement.Entity.RecordStatus;

import java.time.LocalDateTime;

public class BorrowRecordDTO {
    public Long userRecordId;
    public Long bookRecordId;
    public Long requestId;

    public BorrowRecordDTO(){

    }

    public BorrowRecordDTO(Long bookRecordId, Long requestId, Long userRecordId) {
        this.bookRecordId = bookRecordId;
        this.requestId = requestId;
        this.userRecordId = userRecordId;
    }

    public Long getBookRecordId() {
        return bookRecordId;
    }

    public void setBookRecordId(Long bookRecordId) {
        this.bookRecordId = bookRecordId;
    }

    public Long getUserRecordId() {
        return userRecordId;
    }

    public void setUserRecordId(Long userRecordId) {
        this.userRecordId = userRecordId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
}
