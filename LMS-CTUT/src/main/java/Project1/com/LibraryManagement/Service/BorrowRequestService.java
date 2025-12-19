package Project1.com.LibraryManagement.Service;

import Project1.com.LibraryManagement.DTO.BorrowRequestDTO;
import Project1.com.LibraryManagement.Entity.BorrowRecord;
import Project1.com.LibraryManagement.Entity.BorrowRequest;

import java.util.List;

public interface BorrowRequestService {
    BorrowRequest createRequest(BorrowRequestDTO borrowRequestDTO);
    List<BorrowRequest> getAllRequest();
    public Long deleteRequest(Long id);
//    public void autoDeleteExpiredRequests();
}
