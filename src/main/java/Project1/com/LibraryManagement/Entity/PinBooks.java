package Project1.com.LibraryManagement.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pinbooks")
public class PinBooks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Books books;

    private String bookCode;

    private String bookName;

    private String author;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;




    private String isbn;

    private String imageURL;

    private LocalDateTime pinnedAt;
    private int quantity;

    public PinBooks(String author, String bookCode, String bookName, Books books, Category category, String imageURL, String isbn, LocalDateTime pinnedAt, int quantity, Users users) {
        this.author = author;
        this.bookCode = bookCode;
        this.bookName = bookName;
        this.books = books;
        this.category = category;
        this.imageURL = imageURL;
        this.isbn = isbn;
        this.pinnedAt = pinnedAt;
        this.quantity = quantity;
        this.users = users;
    }

    public PinBooks(){

    }

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

    public Books getBooks() {
        return books;
    }

    public void setBooks(Books books) {
        this.books = books;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public LocalDateTime getPinnedAt() {
        return pinnedAt;
    }

    public void setPinnedAt(LocalDateTime pinnedAt) {
        this.pinnedAt = pinnedAt;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
