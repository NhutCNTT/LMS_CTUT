package Project1.com.LibraryManagement.Controller;

import Project1.com.LibraryManagement.Entity.Roles;
import Project1.com.LibraryManagement.Entity.Users;
import Project1.com.LibraryManagement.Repository.UserRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepos userRepos;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String SUPER_ADMIN_EMAIL = "admin@gmail.com";

    @GetMapping
    public String listUsers(Model model) {
        List<Users> users = userRepos.findAll();

        model.addAttribute("users", users);
        model.addAttribute("roles", Roles.values()); // USERS, ADMIN, Librarian
        model.addAttribute("newUser", new Users());
        return "administrator/users";   // templates/administrator/users.html
    }

    // 2) Cập nhật role cho 1 user (trừ super admin)
    @PostMapping("/{id}/role")
    public String updateUserRole(@PathVariable Long id,
                                 @RequestParam("role") Roles role) {

        Users user = userRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (SUPER_ADMIN_EMAIL.equalsIgnoreCase(user.getEmail())) {
            return "redirect:/admin/users?superAdminError";
        }

        user.setRoles(role);
        userRepos.save(user);

        return "redirect:/admin/users?updated";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute("newUser") Users user, RedirectAttributes redirectAttributes) {
        if (userRepos.findByEmail(user.getEmail()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Email " + user.getEmail() + " đã tồn tại!");
            return "redirect:/admin/users";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepos.save(user);

        redirectAttributes.addFlashAttribute("success", "Đã thêm tài khoản mới thành công!");
        return "redirect:/admin/users";
    }


    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Users user = userRepos.findById(id).orElse(null);

        if (user != null) {
            if (SUPER_ADMIN_EMAIL.equals(user.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "Không thể xóa Super Admin!");
            } else {
                userRepos.delete(user);
                redirectAttributes.addFlashAttribute("success", "Đã xóa user thành công.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "User không tồn tại!");
        }
        return "redirect:/admin/users";
    }
}
