package Project1.com.LibraryManagement.Service;

import Project1.com.LibraryManagement.DTO.BorrowRecordDTO;
import Project1.com.LibraryManagement.DTO.BorrowingDTO;
import Project1.com.LibraryManagement.DTO.BorrowingResponseDTO;
import Project1.com.LibraryManagement.Entity.BorrowRecord;
import Project1.com.LibraryManagement.Entity.Borrowing;

import java.util.List;

public interface BorrowingService {
    Borrowing createBorrowing(BorrowingDTO borrowingDTO);
//    public List<BorrowingResponseDTO> getAllBorrowingDTO();
public List<Borrowing> getAllBorrowing();
    public void autoDeleteExpiredBorrowing();
}
