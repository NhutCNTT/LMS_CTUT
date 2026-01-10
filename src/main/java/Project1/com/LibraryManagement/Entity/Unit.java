package Project1.com.LibraryManagement.Entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="unit")
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "established_date")
    @Temporal(TemporalType.DATE)
    private Date establishedDate;

    public Unit() {
    }

    public Unit(Long id, String name, String description, Date establishedDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.establishedDate = establishedDate;
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

    public Date getEstablishedDate() {
        return establishedDate;
    }

    public void setEstablishedDate(Date establishedDate) {
        this.establishedDate = establishedDate;
    }
}
