package Project1.com.LibraryManagement.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "borrowing")
public class Borrowing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public Users users;
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    public Books books;
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;
    private LocalDateTime createAt = LocalDateTime.now();
    @Column(name = "deadline")
    private LocalDateTime deadline;
    @Enumerated(EnumType.STRING)
    private BorrowingStatus borrowingStatus;
    public Borrowing(){

    }

    public Borrowing(Books books, BorrowingStatus borrowingStatus, LocalDateTime createAt, LocalDateTime deadline, Long id, RequestStatus requestStatus, Users users) {
        this.books = books;
        this.borrowingStatus = borrowingStatus;
        this.createAt = createAt;
        this.deadline = deadline;
        this.id = id;
        this.requestStatus = requestStatus;
        this.users = users;
    }

    public Books getBooks() {
        return books;
    }

    public void setBooks(Books books) {
        this.books = books;
    }

    public BorrowingStatus getBorrowingStatus() {
        return borrowingStatus;
    }

    public void setBorrowingStatus(BorrowingStatus borrowingStatus) {
        this.borrowingStatus = borrowingStatus;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }
}
