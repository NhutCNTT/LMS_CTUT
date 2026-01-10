package Project1.com.LibraryManagement.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_record")
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Người mượn
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    // Cuốn sách vật lý
    @ManyToOne
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopy bookCopy;

    // Request gốc (nếu có)
    @OneToOne
    @JoinColumn(name = "borrow_request_id")
    private BorrowRequest borrowRequest;

    private LocalDateTime borrowedDate = LocalDateTime.now();
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    private RecordStatus status = RecordStatus.BORROWED;

    private String note;

    @Column(name = "fine_amount")
    private Double fineAmount = 0.0;

    @Transient
    private Double realTimeFine;

    public BorrowRecord() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public BookCopy getBookCopy() {
        return bookCopy;
    }

    public void setBookCopy(BookCopy bookCopy) {
        this.bookCopy = bookCopy;
    }

    public BorrowRequest getBorrowRequest() {
        return borrowRequest;
    }

    public void setBorrowRequest(BorrowRequest borrowRequest) {
        this.borrowRequest = borrowRequest;
    }

    public LocalDateTime getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(LocalDateTime borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    // ⭐ QUAN TRỌNG
    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(Double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public Double getRealTimeFine() {
        return realTimeFine;
    }

    public void setRealTimeFine(Double realTimeFine) {
        this.realTimeFine = realTimeFine;
    }
}
