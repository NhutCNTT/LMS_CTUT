package Project1.com.LibraryManagement.Config;

import Project1.com.LibraryManagement.Entity.Roles;
import Project1.com.LibraryManagement.Entity.Unit;
import Project1.com.LibraryManagement.Entity.Users;
import Project1.com.LibraryManagement.Repository.UnitRepos;
import Project1.com.LibraryManagement.Repository.UserRepos;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class InitAdminConfig {

    @Bean
    CommandLineRunner initAdmin(UserRepos userRepos, PasswordEncoder passwordEncoder, UnitRepos unitRepos) {
        return args -> {

            // ❗ Nếu chưa có admin thì tạo
            if (!userRepos.existsByEmail("admin@gmail.com")) {

                Users admin = new Users();
                admin.setFullName("System Administrator");
                admin.setEmail("admin@gmail.com");

                // Mật khẩu mong muốn: 123 (sẽ được mã hoá qua BCrypt)
                admin.setPassword(passwordEncoder.encode("123"));

                admin.setPhoneNumber("0123456789");
                Unit unit = unitRepos.findByName("Phòng quản lý")
                        .orElseGet(() -> {
                            Unit u = new Unit();
                            u.setName("Phòng quản lý");
                            return unitRepos.save(u);
                        });
                admin.setUnit(unit);
                admin.setAddress("CTUT");
                admin.setDateOfBirth(LocalDate.of(2000, 1, 1));

                // QUAN TRỌNG: gán quyền ADMIN
                admin.setRoles(Roles.ADMIN);

                userRepos.save(admin);

                System.out.println("⚡ Admin created: admin@gmail.com / 123");
            } else {
                System.out.println("✔ Admin account already exists.");
            }
        };
    }
}
