package Project1.com.LibraryManagement.DTO;

import java.time.LocalDateTime;

public class BorrowRecordResponeDTO {
    private Long recordId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String bookCode;
    private String bookName;
    private String status;
    private LocalDateTime borrowedDate;
    private LocalDateTime returnDate;
    private String recordStatus;
    private String note;

    public BorrowRecordResponeDTO() {
    }

    public BorrowRecordResponeDTO(String email, String bookCode, String bookName, LocalDateTime borrowedDate, String fullName, String note, String phoneNumber, Long recordId, String recordStatus, LocalDateTime returnDate, String status) {

        this.email = email;
        this.bookCode = bookCode;
        this.bookName = bookName;
        this.borrowedDate = borrowedDate;
        this.fullName = fullName;
        this.note = note;
        this.phoneNumber = phoneNumber;
        this.recordId = recordId;
        this.recordStatus = recordStatus;
        this.returnDate = returnDate;
        this.status = status;
    }
    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public LocalDateTime getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(LocalDateTime borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
