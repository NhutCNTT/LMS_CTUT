package Project1.com.LibraryManagement.DTO;

import lombok.Data;

public class BorrowRequestDTO {
   public Long userId;
   public Long bookId;


   public BorrowRequestDTO(){

   }

   public BorrowRequestDTO(Long bookId, Long userId) {
      this.bookId = bookId;
      this.userId = userId;
   }

   public Long getBookId() {
      return bookId;
   }

   public void setBookId(Long bookId) {
      this.bookId = bookId;
   }

   public Long getUserId() {
      return userId;
   }

   public void setUserId(Long userId) {
      this.userId = userId;
   }
}


