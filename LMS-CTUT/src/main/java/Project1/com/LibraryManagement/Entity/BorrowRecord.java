package Project1.com.LibraryManagement.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_record")
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Books books;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "borrow_request_id", nullable = true)
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

    public BorrowRecord(){}

    public BorrowRecord(Books books, LocalDateTime borrowedDate, BorrowRequest borrowRequest, LocalDateTime dueDate, Double fineAmount, Long id, String note, Double realTimeFine, LocalDateTime returnDate, RecordStatus status, Users users) {
        this.books = books;
        this.borrowedDate = borrowedDate;
        this.borrowRequest = borrowRequest;
        this.dueDate = dueDate;
        this.fineAmount = fineAmount;
        this.id = id;
        this.note = note;
        this.realTimeFine = realTimeFine;
        this.returnDate = returnDate;
        this.status = status;
        this.users = users;
    }

    public Books getBooks() {
        return books;
    }

    public void setBooks(Books books) {
        this.books = books;
    }

    public LocalDateTime getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(LocalDateTime borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public BorrowRequest getBorrowRequest() {
        return borrowRequest;
    }

    public void setBorrowRequest(BorrowRequest borrowRequest) {
        this.borrowRequest = borrowRequest;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(Double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Double getRealTimeFine() {
        return realTimeFine;
    }

    public void setRealTimeFine(Double realTimeFine) {
        this.realTimeFine = realTimeFine;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }
}
