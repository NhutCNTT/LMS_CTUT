package Project1.com.LibraryManagement.Controller;

import Project1.com.LibraryManagement.Entity.Books;
import Project1.com.LibraryManagement.Entity.PinBooks;
import Project1.com.LibraryManagement.Entity.Users;
import Project1.com.LibraryManagement.Entity.Author;
import Project1.com.LibraryManagement.Repository.BookRepos;
import Project1.com.LibraryManagement.Repository.PinBookRepos;
import Project1.com.LibraryManagement.Service.BookService;
import Project1.com.LibraryManagement.Service.PinBookImpl;
import Project1.com.LibraryManagement.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/user")
public class PinBookController {
    @Autowired
    public UserService userService;
    @Autowired
    public BookRepos bookRepos;
    @Autowired
    public BookService bookService;
    @Autowired
    public PinBookRepos pinBookRepos;



    @GetMapping("/pinbooks")
    public String viewPinBooks(Principal principal, Model model){
        String email;

        if(principal instanceof OAuth2AuthenticationToken oauthToken){
            OAuth2User oauthUser = oauthToken.getPrincipal();
            email = oauthUser.getAttribute("email");
        } else if(principal instanceof UsernamePasswordAuthenticationToken authToken){
            UserDetails userDetails = (UserDetails) authToken.getPrincipal();
            email = userDetails.getUsername();
        } else {
            throw new RuntimeException("Unknown authentication type");
        }

        Users users = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cannot find email"));
        List<PinBooks> pinBooksList = pinBookRepos.findByUsers_Id(users.getId());
        model.addAttribute("pinBooksList", pinBooksList);
        return "user/pinbooks";
    }


    @PostMapping("/addPin/{id}")
    public String addPin(@AuthenticationPrincipal UserDetails userDetails,
                         @PathVariable("id") Long idBook,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        Users users = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Cannot find email"));
        Books books = bookService.findById(idBook)
                .orElseThrow(() -> new RuntimeException("Cannot find book id"));

        PinBooks pinBooks = new PinBooks();
        pinBooks.setBooks(books);
        pinBooks.setUsers(users);
        pinBooks.setBookCode(books.getBookCode());
        pinBooks.setBookName(books.getBookName());

        String authorNames = books.getAuthors().stream()
                .map(Author::getName)
                .collect(java.util.stream.Collectors.joining(", "));
        pinBooks.setAuthor(authorNames);


        pinBooks.setCategory(books.getCategory());
        pinBooks.setIsbn(books.getIsbn());
        pinBooks.setImageURL(books.getImage());
        pinBooks.setPinnedAt(LocalDateTime.now());


        pinBookRepos.save(pinBooks);

        redirectAttributes.addFlashAttribute("message", "Pin book success");
        return "redirect:/user/pinbooks";
    }

    @PostMapping("/deletePin/{id}")
    public String deleteBag(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("id") Long idBook, RedirectAttributes redirectAttributes){
        Users users = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(()-> new RuntimeException("Cannot find email"));
        pinBookRepos.deleteById(idBook);
        redirectAttributes.addFlashAttribute("message","Remove book to pin successfull");
        return "redirect:/user/pinbooks";
    }

}
