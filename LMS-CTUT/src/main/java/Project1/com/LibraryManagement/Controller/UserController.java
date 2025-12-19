package Project1.com.LibraryManagement.Controller;

import Project1.com.LibraryManagement.Entity.*;
import Project1.com.LibraryManagement.Repository.BookRepos;
import Project1.com.LibraryManagement.Repository.TokenRepos;
import Project1.com.LibraryManagement.Repository.UserRepos;
import Project1.com.LibraryManagement.Service.BorrowRecordService;
import Project1.com.LibraryManagement.Service.CustomUserDetailService;
import Project1.com.LibraryManagement.Service.UserService;
import Project1.com.LibraryManagement.Service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    public UserRepos usersRepos;
    @Autowired
    public UserService userService;
    @Autowired
    public PasswordEncoder passwordEncoder;
    @Autowired
    public CustomUserDetailService userDetailService;
    @Autowired
    public TokenRepos tokenRepos;
    @Autowired
    public BookRepos bookRepos;
    @Autowired
    public BookService bookService;
    @Autowired
    public BorrowRecordService borrowRecordService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/home")
    public String Home(){
        return "user/home";
    }
    @GetMapping("/announcement")
    public String annoucement(){
        return "user/announcement";
    }
    @GetMapping("/documents")
    public String documents(){
        return "user/documents";
    }
    @GetMapping("/feedback")
    public String feedback(){
        return "user/feedback";
    }
    @GetMapping("/introduce")
    public String introduce(){
        return "user/introduce";
    }
    @GetMapping("/lookup")
    public String lookup(){
        return "user/lookup";
    }
    @GetMapping("/support")
    public String support(){
        return "user/support";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)){
            return "redirect:/user/home";  //return home page when login success
        }
        if (error != null) {
            model.addAttribute("ErrorLogin", "Email hoặc mật khẩu không chính xác, vui lòng thử lại!");
        }
        if (logout != null) {
            model.addAttribute("SuccessLogout", "Bạn đã đăng xuất thành công!");
        }

        return "user/login"; //when fail return login page
    }

    @GetMapping("/register")
    public String signup(){
        return "user/register";
    }

    @PostMapping("/saveUsers")
    public String saveUsers(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("faculties") String faculties,
            @RequestParam("dayofBirth") Integer dayofBirth,
            @RequestParam("monthofBirth") Integer monthofBirth,
            @RequestParam("yearofBirth") Integer yearofBirth,
            @RequestParam("address") String address,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,

            RedirectAttributes redirectAttributes,
            Model model) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("ErrorPassword", "Passwords do not match");
            return "redirect:/user/register";
        }
        if (!phoneNumber.matches("\\d{10}")) {
            redirectAttributes.addFlashAttribute("ErrorPhoneNumber", "Phone number must be 10 digits");
            return "user/register";
        }

        try {
            if (usersRepos.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("ErrorExistsEmail", "This Account Already Exists");
                return "user/register";
            }
            LocalDate dateOfBirth;
            try {
                dateOfBirth = LocalDate.of(yearofBirth, monthofBirth, dayofBirth);
            } catch (DateTimeException ex) {
                redirectAttributes.addFlashAttribute("ErrorDateTime", "Invalid Date Of Birth");
                return "user/register";
            }
            Users users = new Users();
            users.setEmail(email);
            users.setPassword(passwordEncoder.encode(password));
            users.setFullName(fullName);
            users.setPhoneNumber(phoneNumber);
            users.setFaculties(faculties);
            users.setAddress(address);
            users.setDateOfBirth(dateOfBirth);
            users.setRoles(Roles.USERS);
            Users saved = userService.saveUser(users);

            if (saved == null || saved.getId() == null) {
                redirectAttributes.addFlashAttribute("ErrorCreate", "Tạo tài khoản thất bại! Hãy thử lại.");
                return "user/register";
            }
            redirectAttributes.addFlashAttribute("SuccessCreate", "Tạo tài khoản thành công.");
            return "redirect:/user/login";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("ErrorLogin", "Error creating account: " + e.getMessage());
            return "user/register";
        }
    }
    @GetMapping("/forgotPassword")
    public String forgotPassword(){
        return "/user/forgotPassword";
    }
    
    @PostMapping("/forgotPassword")
    public String forgotPasswordProcess(@RequestParam("email") String email) {

        Users users = usersRepos.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email: " + email));

        userDetailService.sendEmail(users); // chạy async, không return gì

        return "redirect:/user/forgotPassword?success";
    }


    @GetMapping("/resetPassword/{token}")
    public String resetPassword(@PathVariable String token, Model model) {
        PasswordResetToken passwordResetToken = tokenRepos.findByToken(token);

        if (passwordResetToken != null && !userDetailService.hasExpired(passwordResetToken.getExpiredDateTime())) {
            model.addAttribute("email", passwordResetToken.getUsers().getEmail());
            model.addAttribute("token", token);
            return "user/resetPassword";
        }

        return "redirect:/user/forgotPassword?error";
    }

    @PostMapping("/resetPassword")
    public String resetPasswordProcess(@RequestParam("token") String token,
                                       @RequestParam("email") String email,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorPasswordConfirm", "This password is not match!");
            return "redirect:/user/resetPassword/" + token;
        }

        PasswordResetToken passwordResetToken = tokenRepos.findByToken(token);
        if (passwordResetToken == null || userDetailService.hasExpired(passwordResetToken.getExpiredDateTime())) {
            redirectAttributes.addFlashAttribute("errorToken", "Token is invalid or expired!");
            return "redirect:/user/forgotPassword";
        }


        Users user = passwordResetToken.getUsers();
        user.setPassword(passwordEncoder.encode(password));
        usersRepos.save(user);

        tokenRepos.delete(passwordResetToken);

        redirectAttributes.addFlashAttribute("success", "Password reset successful! Please login.");
        return "redirect:/user/login";
    }


    @GetMapping("/search")
    public String searchOnNewTab(Model model,
                                 @RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "status", required = false) String status,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int pageSize) {

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

        return "user/search";
    }
    @GetMapping("/suggest")
    @ResponseBody
    public List<String> suggest(@RequestParam String keyword) {
        return bookService.suggest(keyword);
    }

    @GetMapping("/books/{id}")
    public String viewBookDetail(@PathVariable Long id, Model model, Principal principal) {
        Books book = bookService.getBookById(id);
        model.addAttribute("book", book);

        if (principal != null) {
            Users user = userService.findByEmail(principal.getName())
                            .orElseThrow(()-> new RuntimeException("Not found user"));
            model.addAttribute("user", user);
        }

        return "user/detail";
    }

    @GetMapping("/profile")
    public String profile(Model model,Principal principal) {
        String email;
        if(principal instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken){
            OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();
            email = oAuth2User.getAttribute("email");
        } else if (principal instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken){
            UserDetails userDetails = (UserDetails)  usernamePasswordAuthenticationToken.getPrincipal();
            email = userDetails.getUsername();
        }else {
            throw  new RuntimeException("Unknown authentication type");
        }
        Users usersInfo = usersRepos.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Can not find email"));
        model.addAttribute("usersInfo",usersInfo);
        Long userId = usersInfo.getId();
        Long totalBorrow  = borrowRecordService.getTotalBorrow(userId);
        Long totalBorrowing = borrowRecordService.getCurrentlyBorrowing(userId);
        Long totalLate  = borrowRecordService.getTotalLate(userId);
        model.addAttribute("totalBorrow",totalBorrow);
        model.addAttribute("totalBorrowing",totalBorrowing);
        model.addAttribute("totalLate",totalLate);
        Users userProfile = usersRepos.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("Can not find email"));
        model.addAttribute("userProfile", userProfile);
        return "user/profile";
    }



    @GetMapping("/history")
    public String history(Model model,Principal principal) {
        String email;
        if(principal instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken){
            OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();
            email = oAuth2User.getAttribute("email");
        } else if (principal instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken){
            UserDetails userDetails = (UserDetails)  usernamePasswordAuthenticationToken.getPrincipal();
            email = userDetails.getUsername();
        }else {
            throw  new RuntimeException("Unknown authentication type");
        }
        Users usersInfo = usersRepos.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Can not find email"));
        model.addAttribute("usersInfo",usersInfo);
        Long userId = usersInfo.getId();
        Long totalBorrow  = borrowRecordService.getTotalBorrow(userId);
        Long totalBorrowing = borrowRecordService.getCurrentlyBorrowing(userId);
        Long totalLate  = borrowRecordService.getTotalLate(userId);
        model.addAttribute("totalBorrow",totalBorrow);
        model.addAttribute("totalBorrowing",totalBorrowing);
        model.addAttribute("totalLate",totalLate);
        List<BorrowRecord> historyList = borrowRecordService.getHistory(userId);
        model.addAttribute("historyList",historyList);
        return "user/history";
    }

    // PHẦN ĐIỀU HƯỚNG GIỚI THIỆU
    @GetMapping("/intro/rules")
    public String rules() {
        // HTML đang nằm ở: templates/user/rules.html
        return "user/rules";
    }
    @GetMapping("/intro/about")
    public String introAbout() {
        return "user/about"; // trỏ tới templates/user/about.html
    }

    @GetMapping("/intro/org")
    public String org() {
        return "user/org"; // trỏ tới templates/user/org.html
    }

    @GetMapping("/lookup/search")
    public String lookupSearch() {
        return "user/lookup-search";  // templates/user/lookup-search.html
    }



}
