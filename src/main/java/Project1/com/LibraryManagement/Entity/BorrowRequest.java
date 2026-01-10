package Project1.com.LibraryManagement.Entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_request")
public class    BorrowRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public Users users;
    @ManyToOne
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopy bookCopy;
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();


    public BorrowRequest(){

    }

    public BorrowRequest(BookCopy bookCopy, LocalDateTime createdAt,  RequestStatus requestStatus, Users users) {
        this.bookCopy = bookCopy;
        this.createdAt = createdAt;
        this.requestStatus = requestStatus;
        this.users = users;
    }

    public BookCopy getBookCopy() {
        return bookCopy;
    }

    public void setBookCopy(BookCopy bookCopy) {
        this.bookCopy = bookCopy;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
