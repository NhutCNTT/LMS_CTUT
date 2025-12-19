package Project1.com.LibraryManagement.Service;

import Project1.com.LibraryManagement.Entity.PinBooks;

import java.util.List;

public interface PinBookService {
    public List<PinBooks> findUserById(Long id);
    void deletePin(Long id);
}
