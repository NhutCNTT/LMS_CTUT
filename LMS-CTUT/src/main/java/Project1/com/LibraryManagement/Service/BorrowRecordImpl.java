    package Project1.com.LibraryManagement.Service;

    import Project1.com.LibraryManagement.DTO.BorrowRecordDTO;
    import Project1.com.LibraryManagement.DTO.BorrowRecordResponeDTO;
    import Project1.com.LibraryManagement.Entity.*;
    import Project1.com.LibraryManagement.Repository.*;
    import org.hibernate.annotations.Array;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.temporal.ChronoUnit;
    import java.util.List;
    import java.time.temporal.ChronoUnit;


    @Service
    public class BorrowRecordImpl implements BorrowRecordService {
        @Autowired
        public UserRepos userRepos;
        @Autowired
        public BookRepos bookRepos;
        @Autowired
        public BorrowRecordRepos borrowRecordRepos;
        @Autowired
        public BorrowRequestRepos borrowRequestRepos;
        @Autowired
        public BorrowingRepos borrowingRepos;

        @Override
        @Transactional
        public BorrowRecord confirmTaken(Long borrowId) {

            Borrowing borrowing = borrowingRepos.findById(borrowId)
                    .orElseThrow(() -> new RuntimeException("Borrowing not found"));

            BorrowRecord record = new BorrowRecord();
            record.setUsers(borrowing.getUsers());
            record.setBooks(borrowing.getBooks());
            record.setBorrowedDate(LocalDateTime.now());
            record.setReturnDate(LocalDateTime.now().plusDays(1)); // tạm thời 1 ngày (7)
            record.setReturnDate(null);
            record.setStatus(RecordStatus.BORROWED);

            borrowRecordRepos.save(record);
            borrowingRepos.delete(borrowing);

            return record;
        }


        @Override
        public List<BorrowRecordResponeDTO> getAllRecordDTO() {
            return borrowRecordRepos.findAll().stream()
                    .map(record -> new BorrowRecordResponeDTO(
                            record.getUsers().getEmail(),
                            record.getBooks().getBookCode(),
                            record.getBooks().getBookName(),
                            record.getBorrowedDate(),
                            record.getUsers().getFullName(),
                            record.getNote(),
                            record.getUsers().getPhoneNumber(),
                            record.getId(),
                            record.getStatus().name(),
                            record.getReturnDate(),
                            record.getBooks().getStatus()
                            ))
                    .toList();
        }


        @Override
        public List<BorrowRecord> getAllRecord() {
            List<BorrowRecord> list = borrowRecordRepos.findAll();

            for (BorrowRecord r : list) {
                r.setRealTimeFine(getRealTimeFine(r));
            }

            return list;
        }

        @Override
        public BorrowRecord saveBookBorrow(BorrowRecord borrowRecord){
            return borrowRecordRepos.save(borrowRecord);
        }

        @Override
        public Long getTotalBorrow(Long userId) {
            return borrowRecordRepos.countByUsersId(userId);
        }
        @Override
        public Long getCurrentlyBorrowing(Long userId) {
            return borrowRecordRepos.countByUsersIdAndStatus(userId, RecordStatus.BORROWED);
        }
        @Override
        public Long getTotalLate(Long userId) {
            return borrowRecordRepos.countByUsersIdAndStatus(userId, RecordStatus.LATE);
        }

        @Override
        public List<BorrowRecord> getHistory(Long userId) {
            return borrowRecordRepos.findByUsersId(userId);
        }

        @Override
        public Double getRealTimeFine(BorrowRecord record) {

            if (record.getStatus() == RecordStatus.RETURNED) {
                return record.getFineAmount() != null ? record.getFineAmount() : 0.0;
            }

            if (record.getDueDate() != null) {
                LocalDate today = LocalDate.now();
                LocalDate deadline = record.getDueDate().toLocalDate();

                if (today.isAfter(deadline)) {
                    long overdueDays = ChronoUnit.DAYS.between(deadline, today);
                    return overdueDays * 5000.0;
                }
            }

            return 0.0;
        }

        @Override
        public void processReturn(BorrowRecord borrowRecord) {
            LocalDateTime returnTime = LocalDateTime.now();
            borrowRecord.setReturnDate(returnTime);

            long finePerDay = 5000;

            if (borrowRecord.getDueDate() != null && returnTime.isAfter(borrowRecord.getDueDate())) {

                long overdueDays = ChronoUnit.DAYS.between(
                        borrowRecord.getDueDate().toLocalDate(),
                        returnTime.toLocalDate());

                if (overdueDays > 0) {
                    borrowRecord.setFineAmount((double) (overdueDays * finePerDay));
                } else {
                    borrowRecord.setFineAmount(0.0);
                }
            } else {
                borrowRecord.setFineAmount(0.0);
            }
            Books book = borrowRecord.getBooks();
            book.setQuantity(book.getQuantity() + 1);
            bookRepos.save(book);

            borrowRecord.setStatus(RecordStatus.RETURNED);
            borrowRecordRepos.save(borrowRecord);
        }


    }
