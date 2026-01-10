package Project1.com.LibraryManagement.Controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import Project1.com.LibraryManagement.Repository.UserRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdministratorController {

    @Autowired
    private UserRepos userRepos;

    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/admin/dashboard";
        }

        return "administrator/loginAd";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        long totalUsers = userRepos.count();
        model.addAttribute("totalUsers", totalUsers);

        return "administrator/dashboard";
    }
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "administrator/accessDenied";
    }
    @GetMapping("/switch-account")
    public String switchAccount(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/admin/login";
    }




}
