package Project1.com.LibraryManagement.Controller;

import Project1.com.LibraryManagement.DTO.BorrowRecordDTO;
import Project1.com.LibraryManagement.DTO.BorrowRecordResponeDTO;
import Project1.com.LibraryManagement.DTO.BorrowingDTO;
import Project1.com.LibraryManagement.DTO.BorrowingResponseDTO;
import Project1.com.LibraryManagement.Entity.*;
import Project1.com.LibraryManagement.Repository.*;
import Project1.com.LibraryManagement.Service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.management.relation.Role;
import javax.swing.text.html.Option;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/librarian")
public class LibrarianController {
    @Autowired
    public BookService bookService;
    @Autowired
    public FileUpload fileUpload;
    @Autowired
    public UserService userService;
    @Autowired
    public GoogleBookService googleBookService;
    @Autowired
    public BookRepos bookRepos;
    @Autowired
    public UserRepos userRepos;
    @Autowired
    public PasswordEncoder passwordEncoder;
    @Autowired
    public BorrowRequestService borrowRequestService;
    @Autowired
    public BorrowRecordService borrowRecordService;
    @Autowired
    public CustomUserDetailService customUserDetailService;
    @Autowired
    public BorrowRequestRepos borrowRequestRepos;
    @Autowired
    public BorrowRecordRepos borrowRecordRepos;
    @Autowired
    public BorrowingService borrowingService;
    @Autowired
    public BorrowingRepos borrowingRepos;

    @GetMapping("/loginLib")
    public String loginLibrarian() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/librarian/dashboard";
        }
        return "librarian/loginLib";
    }

    // Trang chính thư viện
    @GetMapping("/dashboard")
    public String libraryDashboard() {
        return "librarian/dashboard";
    }

    // Tùy chọn: map "/" vào thư viện (nếu bạn muốn)
    @GetMapping("/")
    public String home() {
        return "librarian/dashboard";
    }

    // Độc giả
    @GetMapping("/readers")
    public String readers(@RequestParam(value = "id", required = false) Long id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize, Model model) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Users> usersPage = userRepos.findAll(pageable);
        model.addAttribute("keyword", "");
        model.addAttribute("userStatus", "");
        model.addAttribute("faculties", "");
        model.addAttribute("userPageContent", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        if (id != null) {
            Users editUsers = userService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
            model.addAttribute("editUsers", editUsers);
            model.addAttribute("showEditModal", true);
        } else {
            model.addAttribute("editUsers", new Users());
            model.addAttribute("showEditModal", false);
        }
        return "librarian/readers";
    }

    @Transactional
    @GetMapping("/deleteUsers")
    public String deleteUsers(@RequestParam("id") Long id, @RequestParam(value = "page", defaultValue = "0") int page, RedirectAttributes redirectAttributes) {
        userService.deleteUsers(id);
        redirectAttributes.addFlashAttribute("deleteUserSuccess", "Xóa người dùng thành công");
        return "redirect:/librarian/readers?page=" + page;
    }

    @PostMapping("/saveUsers")
    public String saveUsers(@RequestParam("fullName") String fullName, @RequestParam("email") String email, @RequestParam("phoneNumber") String phoneNumber, @RequestParam("faculties") String faculties, @RequestParam("dayofBirth") Integer dayofBirth, @RequestParam("monthofBirth") Integer monthofBirth, @RequestParam("yearofBirth") Integer yearofBirth, @RequestParam("address") String address, @RequestParam("password") String password, @RequestParam("roles") Roles roles, @RequestParam("userStatus") String userStatus, RedirectAttributes redirectAttributes, Model model) {

        if (!phoneNumber.matches("\\d{10}")) {
            redirectAttributes.addFlashAttribute("errorPhoneNumber", "Số điện thoại phải đủ 10 số");
            return "librarian/readers";
        }

        try {
            if (userRepos.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("errorExistsEmail", "Tài khoản này đã tồn tại");
                return "redirect:/librarian/readers";
            }
            LocalDate dateOfBirth;
            try {
                dateOfBirth = LocalDate.of(yearofBirth, monthofBirth, dayofBirth);
            } catch (DateTimeException ex) {
                redirectAttributes.addFlashAttribute("errorDateOfBirth", "Ngày tháng năm sinh không hợp lệ");
                return "user/readers";
            }
            Users users = new Users();
            users.setEmail(email);
            users.setPassword(passwordEncoder.encode(password));
            users.setFullName(fullName);
            users.setPhoneNumber(phoneNumber);
            users.setFaculties(faculties);
            users.setAddress(address);
            users.setDateOfBirth(dateOfBirth);
            users.setUserStatus(userStatus);
            users.setRoles(roles);
            Users saved = userService.saveUser(users);

            if (saved == null || saved.getId() == null) {
                redirectAttributes.addFlashAttribute("errorSaveUser", "Lỗi lưu thông tin người dùng! thử lại");
                return "user/readers";
            }
            redirectAttributes.addFlashAttribute("successSaveUser", "Lưu thông tin người dùng thành công");
            return "redirect:/librarian/readers";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("ErrorAddUser", "Error creating account: " + e.getMessage());
            return "librarian/readers";
        }

    }

    @PostMapping("/saveEditUsers")
    public String saveEditUsers(@RequestParam("id") Long id, @RequestParam("email") String email, @RequestParam("faculties") String faculties, @RequestParam("fullName") String fullName, @RequestParam("phoneNumber") String phoneNumber, @RequestParam("address") String address, @RequestParam("userStatus") String userStatus, @RequestParam("roles") Roles roles, @RequestParam(value = "page", defaultValue = "0") int page, RedirectAttributes redirectAttributes, Model model) {

        try {
            Optional<Users> usersOpt = userService.findById(id);
            if (usersOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorEditUser", "Người dùng không tìm thấy");
                return "redirect:/librarian/readers?page=" + page;
            }

            Users user = usersOpt.get();

            if (userService.existsUser(email) && !user.getEmail().equals(email)) {
                redirectAttributes.addFlashAttribute("errorExistUser", "Tài khoản email này đã tồn tại");
                redirectAttributes.addFlashAttribute("editUser", user);
                return "librarian/readers";
            }
            user.setEmail(email);
            user.setFaculties(faculties);
            user.setFullName(fullName);
            user.setPhoneNumber(phoneNumber);
            user.setAddress(address);
            user.setUserStatus(userStatus);
            user.setRoles(roles);
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("successEditUser", "Cập nhật thông tin thành công!");
            return "redirect:/librarian/readers?page=" + page;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorEditUser", "Error updating user information: " + e.getMessage());
            return "librarian/readers";
        }
    }


    @GetMapping("/searchUsers")
    public String searchUsers(Model model, @RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "userStatus", required = false) String userStatus, @RequestParam(value = "faculties", required = false) String faculties,

                              @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {

        String kw = (keyword != null) ? keyword.trim() : "";
        String st = (userStatus != null) ? userStatus.trim() : "";
        String ft = (faculties != null) ? faculties.trim() : "";

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Users> usersPage;

        if (!kw.isEmpty() || !st.isEmpty() || !ft.isEmpty()) {
            usersPage = userRepos.searchUsers(kw, st, ft, pageable);
        } else {
            usersPage = userRepos.findAll(pageable);
        }

        model.addAttribute("userPageContent", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("keyword", kw);
        model.addAttribute("userStatus", st);
        model.addAttribute("faculties", ft);

        return "librarian/readers";
    }

    @GetMapping("/users/page/{offset}/{pageSize}")
    public List<Users> showAllUserPagination(@PathVariable int offset, @PathVariable int pageSize) {
        Page<Users> usersPage = userService.findUserPagination(offset, pageSize);
        return usersPage.getContent();
    }

    @GetMapping("/exportUsers")
    public void exportUsersToCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=users.csv");
        bookService.exportUsersToCsv(response.getWriter());
    }


    @GetMapping("/books")
    public String showBooks(@RequestParam(value = "id", required = false) Long id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize, Model model) {

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Books> booksPage = bookRepos.findAll(pageable);
        model.addAttribute("keyword", "");
        model.addAttribute("status", "");


        model.addAttribute("bookPageContent", booksPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", booksPage.getTotalPages());

        if (id != null) {
            Books editBooks = bookService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid book ID: " + id));
            model.addAttribute("editBooks", editBooks);
            model.addAttribute("showEditModal", true);
        } else {
            model.addAttribute("editBooks", new Books());
            model.addAttribute("showEditModal", false);
        }

        return "librarian/books";
    }


    @GetMapping("/books/page/{offset}/{pageSize}")
    public List<Books> showAllBookPagination(@PathVariable int offset, @PathVariable int pageSize) {
        Page<Books> booksPage = bookService.findBookPagination(offset, pageSize);
        return booksPage.getContent();
    }


    @PostMapping("/saveBooks")
    public String saveBooks(@RequestParam("bookCode") String bookCode, @RequestParam("bookName") String bookName, @RequestParam("author") String author, @RequestParam("isbn") String ISBN, @RequestParam("category") String category, @RequestParam("price") double price, @RequestParam("quantity") int quantity, @RequestParam("status") String status, @RequestParam("image") MultipartFile multipartFile, @RequestParam("description") String description, @RequestParam("location") String location,Model model, RedirectAttributes redirectAttributes) {
        try {
            if (bookService.existsBooks(bookCode, bookName)) {
                redirectAttributes.addFlashAttribute("errorExistBook", "Cuốn sách này đã tồn tại");
                return "redirect:/librarian/books";
            }
            if (multipartFile.isEmpty()) {
                redirectAttributes.addFlashAttribute("emptyMultipartFile", "Hãy lựa chọn File ");
                return "redirect:/librarian/books";
            }
            String imageURL = fileUpload.uploadFile(multipartFile);
            Books books = new Books();
            books.setBookCode(bookCode);
            books.setBookName(bookName);
            books.setAuthor(author);
            books.setIsbn(ISBN);
            books.setCategory(category);
            books.setPrice(price);
            books.setQuantity(quantity);
            books.setStatus(status);
            books.setImage(imageURL);
            books.setDescription(description);
            books.setLocation(location);
            bookService.saveBooks(books);
            return "redirect:/librarian/books";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorImage", "Error Uploading File: " + e.getMessage());
            return "librarian/books";
        }
    }

    @Transactional
    @GetMapping("/deleteBooks")
    public String deleteBooks(@RequestParam("id") Long id, @RequestParam(value = "page", defaultValue = "0") int page, RedirectAttributes redirectAttributes) {
        bookService.deleteBooks(id);
        redirectAttributes.addFlashAttribute("deleteBookSuccess", "Xóa sách thành công");
        return "redirect:/librarian/books?page=" + page;
    }


    @PostMapping("/saveEditBooks")
    public String saveEditBooks(@RequestParam("id") Long id, @RequestParam("bookCode") String bookCode, @RequestParam("bookName") String bookName, @RequestParam("author") String author, @RequestParam("isbn") String isbn, @RequestParam("category") String category, @RequestParam("price") double price, @RequestParam("quantity") int quantity, @RequestParam("status") String status, @RequestParam(value = "image", required = false) MultipartFile multipartFile, @RequestParam("description") String description, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam("location") String location,RedirectAttributes redirectAttributes, Model model) {
        try {
            Optional<Books> booksOpt = bookService.findById(id);
            if (booksOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorEditBook", "Không tìm thấy sách!");
                return "redirect:/librarian/books?page=" + page;
            }

            Books book = booksOpt.get();

            if (bookService.existsBooks(bookCode, bookName) && !(book.getBookCode().equals(bookCode) && book.getBookName().equals(bookName))) {
                redirectAttributes.addFlashAttribute("errorExistBook", "Cuốn sách này không tồn tại");
                redirectAttributes.addFlashAttribute("editBooks", book);
                return "librarian/books";
            }

            book.setBookCode(bookCode);
            book.setBookName(bookName);
            book.setAuthor(author);
            book.setIsbn(isbn);
            book.setCategory(category);
            book.setPrice(price);
            book.setQuantity(quantity);
            book.setStatus(status);
            book.setDescription(description);
            book.setLocation(location);

            if (multipartFile != null && !multipartFile.isEmpty()) {
                String imageURL = fileUpload.uploadFile(multipartFile);
                book.setImage(imageURL);
            }

            bookService.saveBooks(book);
            redirectAttributes.addFlashAttribute("editBookSuccess", "Cập nhật sách thành công!");
            return "redirect:/librarian/books?page=" + page;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorEditBook", "Cập nhật sách không thành công! " + e.getMessage());
            return "librarian/books";
        }
    }


    //ISBN Additional
    @PostMapping("/addByIsbn")
    public String addBookByIsbn(@RequestParam("isbn") String isbn, RedirectAttributes redirectAttributes) {
        Optional<Books> booksOptional = googleBookService.getBookByIsbn(isbn);
        if (booksOptional.isPresent()) {
            bookService.saveBooks(booksOptional.get());
            redirectAttributes.addFlashAttribute("successISBN", "Đã thêm sách bằng ISBN thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorISBN", "Không tìm thấy sách với ISBN này.");
        }
        return "redirect:/librarian/books";
    }

    @GetMapping("/search")
    public String search(Model model, @RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "status", required = false) String status, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {

        String kw = (keyword != null) ? keyword.trim() : "";
        String st = (status != null) ? status.trim() : "";

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Books> booksPage;

        if (!kw.isEmpty() || !st.isEmpty()) {
            booksPage = bookRepos.searchBooks(kw, st, pageable);
        } else {
            booksPage = bookRepos.findAll(pageable);
        }
        model.addAttribute("bookPageContent", booksPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", booksPage.getTotalPages());
        model.addAttribute("keyword", kw);
        model.addAttribute("status", st);

        return "librarian/books";
    }

    @GetMapping("/export")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=books.csv");
        bookService.exportBooksToCsv(response.getWriter());
    }

    @PostMapping("/import")
    public String importCsv(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            bookService.importBooksFromCsv(file);
            redirectAttributes.addFlashAttribute("importCSVSuccess", "Import CSV thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorImportCSV", "Import CSV thất bại: " + e.getMessage());
        }
        return "redirect:/librarian/books";
    }

    @GetMapping("/suggest")
    @ResponseBody
    public List<String> suggest(@RequestParam String keyword) {
        return bookService.suggest(keyword);
    }



    @GetMapping("/circulation")
    public String circulation(Model model) {
        List<BorrowRequest> borrowRequests = borrowRequestService.getAllRequest();
        model.addAttribute("borrowRequest",borrowRequests);
        List<Borrowing> borrowing = borrowingService.getAllBorrowing();
        model.addAttribute("borrowing", borrowing);
        List<BorrowRecord> borrowRecord = borrowRecordService.getAllRecord();
        model.addAttribute("borrowRecord", borrowRecord);
        List<BorrowRecord> overdueList = borrowRecordService.getAllRecord()
                .stream()
                .filter(r -> borrowRecordService.getRealTimeFine(r) > 0)
                .filter(r -> r.getStatus() != RecordStatus.RETURNED)
                .toList();

        model.addAttribute("overdueList", overdueList);

        return "librarian/circulation";
    }

@PostMapping("/accept")
public String acceptBorrow(@ModelAttribute BorrowingDTO borrowingDTO,
                           @RequestParam("requestId") Long requestId,
                           RedirectAttributes redirectAttributes,
                           BindingResult result) {

    if (result.hasErrors()) {
        redirectAttributes.addFlashAttribute("errorAcceptBorrow", "Lỗi chấp nhận mượn sách");
        return "redirect:/librarian/circulation";
    }

    Borrowing borrowing = borrowingService.createBorrowing(borrowingDTO);
    customUserDetailService.sendMailApprove(
            borrowing.getUsers(),
            borrowing.getCreateAt(),
            borrowing.getDeadline()
    );

    redirectAttributes.addFlashAttribute("successAcceptBorrow", "Ghi nhận mươn sách đã được chấp nhận");

    return "redirect:/librarian/circulation";
}


    @PostMapping("/reject")
    public String rejectBorrow(@RequestParam("requestId") Long requestId) {
        BorrowRequest borrowRequest = borrowRequestRepos.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Borrow request not found"));
        Books book = borrowRequest.getBooks();
        book.setQuantity(book.getQuantity() + 1);
        bookRepos.save(book);
        Users users = borrowRequest.getUsers();
        customUserDetailService.sendMailReject(users);
        borrowRequestRepos.delete(borrowRequest);
        return "redirect:/librarian/circulation?rejectSuccess";
    }

    @PostMapping("/taken")
    public String taken(@RequestParam("borrowId") Long borrowId ,RedirectAttributes redirectAttributes){
        borrowRecordService.confirmTaken(borrowId);
        redirectAttributes.addFlashAttribute("successTakenBook", "Xác nhận đã lấy sách!");
        return "redirect:/librarian/circulation";
    }

    @Transactional
    @PostMapping("/returned")
    public String returned(@RequestParam("ids") List<Long> ids,
                           RedirectAttributes redirectAttributes) {
        try {
            if (ids != null && !ids.isEmpty()) {
                List<BorrowRecord> records = borrowRecordRepos.findAllById(ids);
                for (BorrowRecord record : records) {
                    borrowRecordService.processReturn(record);

                }
                redirectAttributes.addFlashAttribute("successReturn", "Đã trả sách thành công (kèm tính phí phạt nếu có)!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorReturn", "Lỗi trả sách: " + e.getMessage());
        }
        return "redirect:/librarian/circulation";
    }

    @GetMapping("/searchBorrowing")
    public String searchBorrowing(Model model, @RequestParam(value = "keyword",required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int pageSize){
        String kw = (keyword != null) ? keyword.trim(): "";
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Borrowing> borrowingPage;
        if (!kw.isEmpty()) {
            borrowingPage = borrowingRepos.searchBorrowing(kw, pageable);
        } else {
            borrowingPage = borrowingRepos.findAll(pageable);
        }
        model.addAttribute("borrowing", borrowingPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", borrowingPage.getTotalPages());
        model.addAttribute("keyword", kw);
        return "librarian/circulation";
    }

    @GetMapping("/searchBorrowRecord")
    public String searchBorrowRecord(Model model, @RequestParam(value = "keyword",required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int pageSize){
        String kw = (keyword != null) ? keyword.trim(): "";
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<BorrowRecord> borrowRecordPage;
        if (!kw.isEmpty()) {
            borrowRecordPage = borrowRecordRepos.searchBorrowRecord(kw, pageable);
        } else {
            borrowRecordPage = borrowRecordRepos.findAll(pageable);
        }
        model.addAttribute("borrowRecord", borrowRecordPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", borrowRecordPage.getTotalPages());
        model.addAttribute("keyword", kw);
        return "librarian/circulation";
    }

    @PostMapping("/saveBorrowRecord")
    public String saveBorrowRecord(@RequestParam("borrowDate") LocalDateTime borrowDate,
                                   @RequestParam("returnDate") LocalDateTime returnDate,
                                   @RequestParam("fullName") String fullName,
                                   @RequestParam("phoneNumber") String phoneNumber,
                                   @RequestParam("bookCode") String bookCode,
                                   @RequestParam("bookName") String bookName,
                                   @RequestParam("borrowStatus") RecordStatus borrowStatus,
                                   @RequestParam("note") String note,
                                   RedirectAttributes redirectAttributes) {
        try {
            Users user = userService.findByFullNameAndPhoneNumber(fullName, phoneNumber);
            Books book = bookService.findByBookCodeAndBookName(bookCode, bookName);

            BorrowRecord record = new BorrowRecord();
            record.setUsers(user);
            record.setBooks(book);
            record.setBorrowedDate(borrowDate);
            record.setReturnDate(returnDate);
            record.setStatus(borrowStatus);
            record.setNote(note);

            borrowRecordService.saveBookBorrow(record);

            redirectAttributes.addFlashAttribute("successCreateBorrowForm",
                    "Tạo phiếu mượn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorCreateBorrowForm",
                    "Lỗi khi tạo phiếu mượn: " + e.getMessage());
        }

        return "redirect:/librarian/circulation";
    }


    // Thu phí phạt
    @GetMapping("/fees")
    public String fees() {
        return "librarian/fees";
    }

    // Reports
    @GetMapping("/fee-reports")
    public String feeReports() {
        return "librarian/fee-reports";
    }

    @GetMapping("/member-reports")
    public String memberReports() {
        return "librarian/member-reports";
    }

    @GetMapping("/circulation-reports")
    public String circulationReports() {
        return "librarian/circulation-reports";
    }

    @GetMapping("/overdue-reports")
    public String overdueReports() {
        return "librarian/overdue-reports";
    }
}
