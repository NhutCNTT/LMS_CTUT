package Project1.com.LibraryManagement.Controller;

import Project1.com.LibraryManagement.DTO.BorrowRequestDTO;
import Project1.com.LibraryManagement.DTO.BorrowRequestResponseDTO;
import Project1.com.LibraryManagement.Entity.BorrowRequest;
import Project1.com.LibraryManagement.Service.BorrowRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrow")
public class BorrowRequestController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private BorrowRequestService borrowRequestService;

    @PostMapping("/request")
    public ResponseEntity<?> borrow(@RequestBody BorrowRequestDTO dto) {

        try {
            BorrowRequest saved = borrowRequestService.createRequest(dto);

            BorrowRequestResponseDTO response = new BorrowRequestResponseDTO();
            response.setRequestId(saved.getId());

            // USER
            response.setFullName(saved.getUsers().getFullName());
            response.setEmail(saved.getUsers().getEmail());
            response.setPhone(saved.getUsers().getPhoneNumber());

            // BOOK (QUA BOOK COPY)
            response.setBookName(saved.getBookCopy().getBook().getBookName());
            response.setBookCode(saved.getBookCopy().getBook().getBookCode());

            // STATUS + TIME
            response.setBorrowStatus(saved.getRequestStatus().name());
            response.setCreatedAt(saved.getCreatedAt());

            // WEBSOCKET NOTIFY
            simpMessagingTemplate.convertAndSend("/topic/orderBook", response);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
