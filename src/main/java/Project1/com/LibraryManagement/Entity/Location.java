package Project1.com.LibraryManagement.Entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String campus;
    private String building;
    private String floor;
    private String area;
    private String shelf;
    private String rack;

    @Column(name = "location_code", unique = true)
    private String locationCode;

    private String note;

    @OneToMany(mappedBy = "location")
    private List<BookCopy> bookCopies;
    public Location() {}

    public Location(Long id, String campus, String building, String floor, String area, String shelf, String rack, String locationCode, String note, List<BookCopy> bookCopies) {
        this.id = id;
        this.campus = campus;
        this.building = building;
        this.floor = floor;
        this.area = area;
        this.shelf = shelf;
        this.rack = rack;
        this.locationCode = locationCode;
        this.note = note;
        this.bookCopies = bookCopies;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public String getRack() {
        return rack;
    }

    public void setRack(String rack) {
        this.rack = rack;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<BookCopy> getBookCopies() {
        return bookCopies;
    }

    public void setBookCopies(List<BookCopy> bookCopies) {
        this.bookCopies = bookCopies;
    }
}
