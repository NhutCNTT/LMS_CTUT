package Project1.com.LibraryManagement.Entity;

import jakarta.persistence.*;

import java.awt.print.Book;
import java.util.List;

@Entity
@Table(name="category")
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name="name")
  private String name;
  @Column(columnDefinition = "TEXT")
  private String description;
  @OneToMany(mappedBy = "category")
  private List<Books> books;

    public Category() {
    }

    public Category(Long id, String name, String description, List<Books> books) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.books = books;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Books> getBooks() {
        return books;
    }

    public void setBooks(List<Books> books) {
        this.books = books;
    }
}
