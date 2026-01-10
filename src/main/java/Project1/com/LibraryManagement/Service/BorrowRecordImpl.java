package Project1.com.LibraryManagement.Service;

import Project1.com.LibraryManagement.DTO.BorrowRecordResponeDTO;
import Project1.com.LibraryManagement.Entity.*;
import Project1.com.LibraryManagement.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BorrowRecordImpl implements BorrowRecordService {

    @Autowired
    private BorrowRecordRepos borrowRecordRepos;

    @Autowired
    private BorrowingRepos borrowingRepos;

    @Autowired
    private BookCopyRepos bookCopyRepos;

    /* ================= LẤY SÁCH ================= */
    @Override
    @Transactional
    public BorrowRecord confirmTaken(Long borrowingId) {

        Borrowing borrowing = borrowingRepos.findById(borrowingId)
                .orElseThrow(() -> new RuntimeException("Borrowing not found"));

        BookCopy bookCopy = borrowing.getBookCopy();

        BorrowRecord record = new BorrowRecord();
        record.setUsers(borrowing.getUsers());
        record.setBookCopy(bookCopy);
        record.setBorrowedDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(7)); // cấu hình 7 ngày
        record.setStatus(RecordStatus.BORROWED);

        borrowRecordRepos.save(record);
        borrowingRepos.delete(borrowing);

        return record;
    }

    /* ================= DANH SÁCH ================= */
    @Override
    public List<BorrowRecord> getAllRecord() {
        List<BorrowRecord> list = borrowRecordRepos.findAll();
        list.forEach(r -> r.setRealTimeFine(getRealTimeFine(r)));
        return list;
    }

    /* ================= LƯU TRỰC TIẾP ================= */
    @Override
    public BorrowRecord saveBookBorrow(BorrowRecord borrowRecord) {
        return borrowRecordRepos.save(borrowRecord);
    }

    /* ================= DTO ================= */
    @Override
    public List<BorrowRecordResponeDTO> getAllRecordDTO() {
        return borrowRecordRepos.findAll().stream()
                .map(r -> new BorrowRecordResponeDTO(
                        r.getUsers().getEmail(),
                        r.getBookCopy().getBook().getBookCode(),
                        r.getBookCopy().getBook().getBookName(),
                        r.getBorrowedDate(),
                        r.getUsers().getFullName(),
                        r.getNote(),
                        r.getUsers().getPhoneNumber(),
                        r.getId(),
                        r.getStatus().name(),
                        r.getReturnDate(),
                        r.getBookCopy().getStatus().name()
                ))
                .toList();
    }

    /* ================= THỐNG KÊ ================= */
    @Override
    public Long getTotalBorrow(Long userId) {
        return borrowRecordRepos.countByUsers_Id(userId);
    }

    @Override
    public Long getCurrentlyBorrowing(Long userId) {
        return borrowRecordRepos.countByUsers_IdAndStatus(userId, RecordStatus.BORROWED);
    }

    @Override
    public Long getTotalLate(Long userId) {
        return borrowRecordRepos.countByUsers_IdAndStatus(userId, RecordStatus.LATE);
    }

    @Override
    public List<BorrowRecord> getHistory(Long userId) {
        return borrowRecordRepos.findByUsers_Id(userId);
    }

    /* ================= TÍNH PHẠT ================= */
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

    /* ================= TRẢ SÁCH ================= */
    @Override
    @Transactional
    public void processReturn(BorrowRecord record) {

        LocalDateTime now = LocalDateTime.now();
        record.setReturnDate(now);

        if (record.getDueDate() != null && now.isAfter(record.getDueDate())) {
            long overdueDays = ChronoUnit.DAYS.between(
                    record.getDueDate().toLocalDate(),
                    now.toLocalDate()
            );
            record.setFineAmount(overdueDays * 5000.0);
        } else {
            record.setFineAmount(0.0);
        }

        // Trả sách về kệ
        BookCopy bookCopy = record.getBookCopy();
        bookCopy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopyRepos.save(bookCopy);

        record.setStatus(RecordStatus.RETURNED);
        borrowRecordRepos.save(record);
    }
}
