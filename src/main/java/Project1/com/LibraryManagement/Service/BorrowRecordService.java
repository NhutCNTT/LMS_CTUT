package Project1.com.LibraryManagement.Service;

import Project1.com.LibraryManagement.DTO.BorrowRecordDTO;
import Project1.com.LibraryManagement.DTO.BorrowRecordResponeDTO;
import Project1.com.LibraryManagement.Entity.Books;
import Project1.com.LibraryManagement.Entity.BorrowRecord;

import java.util.List;

public interface BorrowRecordService {
    public BorrowRecord confirmTaken(Long borrowId);
    List<BorrowRecordResponeDTO> getAllRecordDTO();
    List<BorrowRecord> getAllRecord();
    public BorrowRecord saveBookBorrow(BorrowRecord borrowRecord);
    public Long getTotalBorrow(Long userId);
    public Long getTotalLate(Long userId);
    public Long getCurrentlyBorrowing(Long userId);
    public List<BorrowRecord> getHistory(Long userId);
    public Double getRealTimeFine(BorrowRecord borrowRecord);
    void processReturn(BorrowRecord borrowRecord);
}
