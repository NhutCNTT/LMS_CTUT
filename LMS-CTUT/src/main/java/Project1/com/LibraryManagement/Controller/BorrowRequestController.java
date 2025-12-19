package Project1.com.LibraryManagement.Controller;

import Project1.com.LibraryManagement.DTO.BorrowRecordDTO;
import Project1.com.LibraryManagement.DTO.BorrowRequestDTO;
import Project1.com.LibraryManagement.DTO.BorrowRequestResponseDTO;
import Project1.com.LibraryManagement.Entity.BorrowRequest;
import Project1.com.LibraryManagement.Service.BorrowRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/borrow")
public class BorrowRequestController {
    @Autowired
    public SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public BorrowRequestService borrowRequestService;

    @PostMapping("/request")
    public ResponseEntity<?> borrow(@RequestBody BorrowRequestDTO dto) {
        try {
            BorrowRequest saved = borrowRequestService.createRequest(dto);

            BorrowRequestResponseDTO response = new BorrowRequestResponseDTO();
            response.setRequestId(saved.getId());
            response.setFullName(saved.getUsers().getFullName());
            response.setEmail(saved.getUsers().getEmail());
            response.setPhone(saved.getUsers().getPhoneNumber());
            response.setBookName(saved.getBooks().getBookName());
            response.setBookCode(saved.getBooks().getBookCode());
            response.setBorrowStatus(saved.getRequestStatus().name());
            response.setCreatedAt(saved.getCreateAt());

            simpMessagingTemplate.convertAndSend("/topic/orderBook", response);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }






}
