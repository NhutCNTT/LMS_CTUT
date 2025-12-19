package Project1.com.LibraryManagement.DTO;

import lombok.Data;

import java.time.LocalDateTime;

public class BorrowRequestResponseDTO {
    private Long requestId;
    private String fullName;
    private String email;
    private String phone;
    private String bookName;
    private String bookCode;
    private String borrowStatus;
    private LocalDateTime createdAt;
    private LocalDateTime deadLine;

    public BorrowRequestResponseDTO(){

    }

    public BorrowRequestResponseDTO(String bookCode, String bookName, String borrowStatus, LocalDateTime createdAt, LocalDateTime deadLine, String email, String fullName, String phone, Long requestId) {
        this.bookCode = bookCode;
        this.bookName = bookName;
        this.borrowStatus = borrowStatus;
        this.createdAt = createdAt;
        this.deadLine = deadLine;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.requestId = requestId;
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

    public String getBorrowStatus() {
        return borrowStatus;
    }

    public void setBorrowStatus(String borrowStatus) {
        this.borrowStatus = borrowStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(LocalDateTime deadLine) {
        this.deadLine = deadLine;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
}
