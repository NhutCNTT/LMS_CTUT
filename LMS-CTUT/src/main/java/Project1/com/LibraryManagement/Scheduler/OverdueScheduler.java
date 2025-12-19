package Project1.com.LibraryManagement.Scheduler;

import Project1.com.LibraryManagement.Entity.BorrowRecord;
import Project1.com.LibraryManagement.Entity.RecordStatus;
import Project1.com.LibraryManagement.Repository.BorrowRecordRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.boot.context.event.ApplicationReadyEvent;                                                    //
import org.springframework.context.event.EventListener;                                                                 //

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class OverdueScheduler {

    @Autowired
    private BorrowRecordRepos borrowRecordRepos;

    // Cron expression: "0 0 0 * * ?" nghĩa là chạy lúc 00:00:00 mỗi ngày
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void scanAndMarkOverdue() {
        System.out.println("--- Bắt đầu quét sách quá hạn ---");

        // 1. Lấy tất cả hồ sơ đang ở trạng thái 'BORROWED'
        List<BorrowRecord> borrowingList = borrowRecordRepos.findByStatus(RecordStatus.BORROWED);
        // Sửa 1: Lấy ngày hôm nay (Bỏ giờ phút)
        LocalDate today = LocalDate.now();      //



        for (BorrowRecord record : borrowingList) {
            // 2. Kiểm tra: Nếu Hạn trả (DueDate) nhỏ hơn thời điểm Hiện tại (Now)
            // Dòng này chỉ so sánh ngày -> Hết ngày hôm đó, sang sáng hôm sau mới bị phạt
            if (record.getDueDate() != null && today.isAfter(record.getDueDate().toLocalDate())) {                      //

                // 3. Đổi trạng thái sang LATE (Trễ hạn)
                record.setStatus(RecordStatus.LATE);

                // 4. Lưu cập nhật
                borrowRecordRepos.save(record);
                System.out.println("Đã đánh dấu quá hạn cho ID: " + record.getId());
            }
        }
        System.out.println("--- Kết thúc quét sách quá hạn ---");
    }
    // 2. THÊM MỚI: Tự động quét ngay khi ứng dụng khởi động xong                                                       //
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void scanOnStartup() {
        System.out.println("--- Server vừa khởi động: Quét bù ngay lập tức ---");
        scanAndMarkOverdue(); // Gọi lại hàm logic ở trên
    }


}