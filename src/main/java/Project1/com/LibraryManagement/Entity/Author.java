package Project1.com.LibraryManagement.Entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

@Entity
@Table(name = "author")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "date_of_death")
    private Date dateOfDeath;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "website")
    private String website;

    @Column(name = "orginazation")
    private String orginazation;

    @Column(name = "ORCID")
    private String ORCID;

    @ManyToMany(mappedBy = "authors")
    private Set<Books> books = new HashSet<>();

    public Author() {}

    // ===== getter / setter =====

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(Date dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getOrginazation() {
        return orginazation;
    }

    public void setOrginazation(String orginazation) {
        this.orginazation = orginazation;
    }

    public String getORCID() {
        return ORCID;
    }

    public void setORCID(String ORCID) {
        this.ORCID = ORCID;
    }

    public Set<Books> getBooks() {
        return books;
    }

    public void setBooks(Set<Books> books) {
        this.books = books;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author)) return false;
        Author author = (Author) o;
        return authorId != null && authorId.equals(author.authorId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
