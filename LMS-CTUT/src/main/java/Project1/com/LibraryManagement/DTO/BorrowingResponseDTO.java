package Project1.com.LibraryManagement.DTO;

import java.time.LocalDateTime;

public class BorrowingResponseDTO {
    private Long borrowId;
    private String fullName;
    private String email;
    private String phone;
    private String bookName;
    private String bookCode;
    private LocalDateTime createdAt;
    private LocalDateTime deadLine;
    public BorrowingResponseDTO(){

    }

    public BorrowingResponseDTO(String bookCode, String bookName, Long borrowId, LocalDateTime createdAt, LocalDateTime deadLine, String email, String fullName, String phone) {
        this.bookCode = bookCode;
        this.bookName = bookName;
        this.borrowId = borrowId;
        this.createdAt = createdAt;
        this.deadLine = deadLine;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
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

    public Long getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(Long borrowId) {
        this.borrowId = borrowId;
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
}
