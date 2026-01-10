package Project1.com.LibraryManagement.Entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.NumberFormat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "books")
public class Books {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bookCode", nullable = false)
    private String bookCode;

    @Column(name = "bookName", nullable = false)
    private String bookName;

    @Column(name = "ISBN", nullable = false)
    private String isbn;

    @Column(name = "price", nullable = false)
    @NumberFormat(pattern = "###.###")
    private double price;

    @Column(nullable = false)
    private String status;

    @Column(name = "image",nullable = false)
    private String image;


    @Lob
    @Column(name = "description",nullable = false, length = 2000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookCopy> bookCopies;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();


    @OneToMany(mappedBy = "books", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PinBooks> pinBooks = new ArrayList<>();

//    @OneToMany(mappedBy = "books", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    public Books(){}

    public Books(Long id, String bookCode, String bookName, String isbn, double price, String status, String image, String description, Category category, List<BookCopy> bookCopies, Set<Author> authors, List<PinBooks> pinBooks) {
        this.id = id;
        this.bookCode = bookCode;
        this.bookName = bookName;
        this.isbn = isbn;
        this.price = price;
        this.status = status;
        this.image = image;
        this.description = description;
        this.category = category;
        this.bookCopies = bookCopies;
        this.authors = authors;
        this.pinBooks = pinBooks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<BookCopy> getBookCopies() {
        return bookCopies;
    }

    public void setBookCopies(List<BookCopy> bookCopies) {
        this.bookCopies = bookCopies;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public List<PinBooks> getPinBooks() {
        return pinBooks;
    }

    public void setPinBooks(List<PinBooks> pinBooks) {
        this.pinBooks = pinBooks;
    }

//    public List<BorrowRecord> getBorrowRecords() {
//        return borrowRecords;
//    }
//
//    public void setBorrowRecords(List<BorrowRecord> borrowRecords) {
//        this.borrowRecords = borrowRecords;
//    }
}
