package Project1.com.LibraryManagement.Controller;

import Project1.com.LibraryManagement.DTO.BorrowingDTO;
import Project1.com.LibraryManagement.Entity.*;
import Project1.com.LibraryManagement.Repository.*;
import Project1.com.LibraryManagement.Service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/librarian")
public class LibrarianController {

    /* ================= AUTOWIRED ================= */

    @Autowired private BookService bookService;
    @Autowired private BookRepos bookRepos;
    @Autowired private AuthorRepos authorRepos;

    @Autowired private UserService userService;
    @Autowired private UserRepos userRepos;
    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private BorrowRequestService borrowRequestService;
    @Autowired private BorrowRequestRepos borrowRequestRepos;

    @Autowired private BorrowingService borrowingService;
    @Autowired private BorrowingRepos borrowingRepos;

    @Autowired private BorrowRecordService borrowRecordService;
    @Autowired private BorrowRecordRepos borrowRecordRepos;

    @Autowired private BookCopyRepos bookCopyRepos;

    @Autowired private FileUpload fileUpload;
    @Autowired private GoogleBookService googleBookService;
    @Autowired private CustomUserDetailService customUserDetailService;

    /* ================= AUTH ================= */

    @GetMapping("/loginLib")
    public String loginLibrarian() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/librarian/dashboard";
        }
        return "librarian/loginLib";
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard() {
        return "librarian/dashboard";
    }

    /* ================= USERS ================= */

    @GetMapping("/readers")
    public String readers(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int pageSize,
                          Model model) {

        Page<Users> usersPage = userRepos.findAll(PageRequest.of(page, pageSize));

        model.addAttribute("userPageContent", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", usersPage.getTotalPages());

        return "librarian/readers";
    }

    /* ================= BOOKS (METADATA ONLY) ================= */

    @GetMapping("/books")
    public String books(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int pageSize,
                        Model model) {

        Page<Books> booksPage = bookRepos.findAll(PageRequest.of(page, pageSize));

        model.addAttribute("bookPageContent", booksPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", booksPage.getTotalPages());

        return "librarian/books";
    }

    @PostMapping("/saveBooks")
    public String saveBooks(@RequestParam String bookCode,
                            @RequestParam String bookName,
                            @RequestParam String authorName,
                            @RequestParam String isbn,
                            @RequestParam String category,
                            @RequestParam double price,
                            @RequestParam String status,
                            @RequestParam MultipartFile image,
                            @RequestParam String description,
                            RedirectAttributes redirect) throws IOException {

        if (bookService.existsBooks(bookCode, bookName)) {
            redirect.addFlashAttribute("error", "Sách đã tồn tại");
            return "redirect:/librarian/books";
        }

        Books book = new Books();
        book.setBookCode(bookCode);
        book.setBookName(bookName);
        book.setIsbn(isbn);
        book.setPrice(price);
        book.setStatus(status);
        book.setDescription(description);

        if (!image.isEmpty()) {
            book.setImage(fileUpload.uploadFile(image));
        }

        Author author = authorRepos.findByName(authorName.trim());
        if (author == null) {
            author = new Author();
            author.setName(authorName.trim());
            author = authorRepos.save(author);
        }
        book.setAuthors(Set.of(author));

        bookService.saveBooks(book);
        redirect.addFlashAttribute("success", "Thêm sách thành công");

        return "redirect:/librarian/books";
    }

    /* ================= CIRCULATION ================= */

    @GetMapping("/circulation")
    public String circulation(Model model) {

        model.addAttribute("borrowRequest", borrowRequestService.getAllRequest());
        model.addAttribute("borrowing", borrowingService.getAllBorrowing());
        model.addAttribute("borrowRecord", borrowRecordService.getAllRecord());

        List<BorrowRecord> overdueList = borrowRecordService.getAllRecord()
                .stream()
                .filter(r -> borrowRecordService.getRealTimeFine(r) > 0)
                .filter(r -> r.getStatus() != RecordStatus.RETURNED)
                .toList();

        model.addAttribute("overdueList", overdueList);

        return "librarian/circulation";
    }

    /* ================= ACCEPT BORROW ================= */

    @PostMapping("/accept")
    public String acceptBorrow(@ModelAttribute BorrowingDTO borrowingDTO,
                               BindingResult result,
                               RedirectAttributes redirect) {

        if (result.hasErrors()) {
            redirect.addFlashAttribute("error", "Lỗi duyệt mượn");
            return "redirect:/librarian/circulation";
        }

        Borrowing borrowing = borrowingService.createBorrowing(borrowingDTO);

        customUserDetailService.sendMailApprove(
                borrowing.getUsers(),
                borrowing.getCreatedAt(),
                borrowing.getDeadline()
        );

        redirect.addFlashAttribute("success", "Đã duyệt mượn sách");
        return "redirect:/librarian/circulation";
    }

    /* ================= REJECT BORROW ================= */

    @PostMapping("/reject")
    @Transactional
    public String rejectBorrow(@RequestParam Long requestId,
                               RedirectAttributes redirect) {

        BorrowRequest request = borrowRequestRepos.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        BookCopy bookCopy = request.getBookCopy();
        bookCopy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopyRepos.save(bookCopy);

        customUserDetailService.sendMailReject(request.getUsers());
        borrowRequestRepos.delete(request);

        redirect.addFlashAttribute("success", "Đã từ chối yêu cầu mượn");
        return "redirect:/librarian/circulation";
    }

    /* ================= RETURN BOOK ================= */

    @PostMapping("/returned")
    @Transactional
    public String returned(@RequestParam List<Long> ids,
                           RedirectAttributes redirect) {

        for (BorrowRecord record : borrowRecordRepos.findAllById(ids)) {
            borrowRecordService.processReturn(record);
        }

        redirect.addFlashAttribute("success", "Đã trả sách thành công");
        return "redirect:/librarian/circulation";
    }

    /* ================= EXPORT / IMPORT ================= */

    @GetMapping("/exportBooks")
    public void exportBooks(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=books.csv");
        bookService.exportBooksToCsv(response.getWriter());
    }

    @PostMapping("/importBooks")
    public String importBooks(@RequestParam MultipartFile file,
                              RedirectAttributes redirect) {

        try {
            bookService.importBooksFromCsv(file);
            redirect.addFlashAttribute("success", "Import CSV thành công");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/librarian/books";
    }
}
