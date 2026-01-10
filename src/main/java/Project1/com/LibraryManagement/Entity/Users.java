package Project1.com.LibraryManagement.Entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "google_id", unique = true)
    private String googleId;
    @Column(name = "email",nullable = false)
    private String email;
    @Column(name = "password",nullable = false)
    private String password;
    @Column(name = "fullname",nullable = false)
    private String fullName;
    @Column(name = "phonenumber",nullable = false)
    private String phoneNumber;
    @Column(name = "address",nullable = false)
    private String address;
    @Column(name = "userstatus", nullable = false)
    private String userStatus = "Hoạt động";
    @Column(name = "date_of_birth",nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING)
    private Roles roles;
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorrowRecord> borrowRecords = new ArrayList<>();
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorrowRequest> borrowRequests = new ArrayList<>();
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PinBooks> pinBooksList = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "unit_id",nullable= false)
    private Unit unit;
    public Users() {

    }

    public Users(Long id, String googleId, String email, String password, String fullName, String phoneNumber, String address, String userStatus, Unit unit, LocalDate dateOfBirth, Roles roles, List<BorrowRecord> borrowRecords, List<BorrowRequest> borrowRequests, List<PinBooks> pinBooksList) {
        this.id = id;
        this.googleId = googleId;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.userStatus = userStatus;
        this.unit = unit;
        this.dateOfBirth = dateOfBirth;
        this.roles = roles;
        this.borrowRecords = borrowRecords;
        this.borrowRequests = borrowRequests;
        this.pinBooksList = pinBooksList;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<BorrowRecord> getBorrowRecords() {
        return borrowRecords;
    }

    public void setBorrowRecords(List<BorrowRecord> borrowRecords) {
        this.borrowRecords = borrowRecords;
    }

    public List<BorrowRequest> getBorrowRequests() {
        return borrowRequests;
    }

    public void setBorrowRequests(List<BorrowRequest> borrowRequests) {
        this.borrowRequests = borrowRequests;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<PinBooks> getPinBooksList() {
        return pinBooksList;
    }

    public void setPinBooksList(List<PinBooks> pinBooksList) {
        this.pinBooksList = pinBooksList;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
