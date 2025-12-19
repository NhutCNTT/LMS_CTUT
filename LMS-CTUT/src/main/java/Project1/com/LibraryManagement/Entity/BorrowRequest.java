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
    @JoinColumn(name = "book_id", nullable = false)
    public Books books;
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;
    private LocalDateTime createAt = LocalDateTime.now();


    public BorrowRequest(){

    }

    public BorrowRequest(Books books, LocalDateTime createAt,  RequestStatus requestStatus, Users users) {
        this.books = books;
        this.createAt = createAt;
        this.requestStatus = requestStatus;
        this.users = users;
    }

    public Books getBooks() {
        return books;
    }

    public void setBooks(Books books) {
        this.books = books;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
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
