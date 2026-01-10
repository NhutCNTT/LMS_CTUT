package Project1.com.LibraryManagement.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrowing")
public class Borrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Người mượn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    // Bản sao sách được mượn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookcopy_id", nullable = false) // ✅ ĐÚNG THEO DB
    private BookCopy bookCopy;

    @Column(name = "create_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "borrowing_status", nullable = false)
    private BorrowingStatus borrowingStatus;

    public Borrowing() {}

    // ===== getter / setter =====

    public Long getId() {
        return id;
    }

    public Users getUsers() {
        return users;
    }

    public BookCopy getBookCopy() {
        return bookCopy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public BorrowingStatus getBorrowingStatus() {
        return borrowingStatus;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public void setBookCopy(BookCopy bookCopy) {
        this.bookCopy = bookCopy;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public void setBorrowingStatus(BorrowingStatus borrowingStatus) {
        this.borrowingStatus = borrowingStatus;
    }
}
