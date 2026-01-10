package Project1.com.LibraryManagement.Service;

import Project1.com.LibraryManagement.Entity.PasswordResetToken;
import Project1.com.LibraryManagement.Entity.Users;
import Project1.com.LibraryManagement.Repository.TokenRepos;
import Project1.com.LibraryManagement.Repository.UserRepos;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    public UserRepos usersRepos;
    @Autowired
    public TokenRepos tokenRepos;
    @Autowired
    public JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;




    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Users users = usersRepos.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User Email Is Not Found"));
        return new org.springframework.security.core.userdetails.User(
                users.getEmail(),
                users.getPassword(),
                java.util.List.of(new SimpleGrantedAuthority("ROLE_" + users.getRoles().name()))
        );
    }

    @Async
    public void sendEmail(Users users){
        try {
            String resetLink= generateResetToken(users);
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(fromEmail);
            simpleMailMessage.setTo(users.getEmail());
            simpleMailMessage.setSubject("Forget Password Link CTUT");
            simpleMailMessage.setText("Please click on this link to Reset your Password :" + resetLink + ". \n\n"
                    + "Regards \n" + "ABC");
            javaMailSender.send(simpleMailMessage);
        }catch ( Exception e){
            e.printStackTrace();
        }
    }

    public String generateResetToken(Users users ){
        UUID uuid = UUID.randomUUID();
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime expiredDateTime = currentDateTime.plusMinutes(5);
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUsers(users);
        resetToken.setToken(uuid.toString());
        resetToken.setExpiredDateTime(expiredDateTime);
        PasswordResetToken token = tokenRepos.save(resetToken);
        if (token != null) {
            String endpointUrl = "http://localhost:8080/user/resetPassword/" + resetToken.getToken();
            return endpointUrl;
        }
        return "";
    }

    public boolean hasExpired(LocalDateTime expiredDateTime){
        LocalDateTime currentDateTime = LocalDateTime.now();
        return expiredDateTime.isBefore(currentDateTime);
    }
    @Scheduled(fixedRate = 300000)
    public void deleteExpiredTokens() {
        tokenRepos.deleteByExpiredDateTimeBefore(LocalDateTime.now());
    }

    @Async
    public void sendMailReject(Users users) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(users.getEmail());
            message.setSubject("Yêu cầu mượn sách bị từ chối");
            message.setText(
                    "Xin chào " + users.getFullName() + ",\n\n" +
                            "Yêu cầu mượn sách của bạn đã bị từ chối.\n" +
                            "Vui lòng đến thư viện để biết thêm chi tiết.\n\n" +
                            "Trân trọng."
            );
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendMailApprove(Users users, LocalDateTime borrowedDate, LocalDateTime borrowExpiredDate) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(users.getEmail());
            message.setSubject("Yêu cầu mượn sách của bạn đã được chấp thuận");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            String formattedBorrow = borrowedDate.format(formatter);
            String formattedExpired = borrowExpiredDate.format(formatter);

            message.setText(
                    "Xin chào " + users.getFullName() + ",\n\n" +
                            "Chúc mừng! Yêu cầu mượn sách của bạn đã được **chấp thuận**.\n\n" +
                            " Thời gian đặt mượn: " + formattedBorrow + "\n" +
                            "Bạn có 24h tính từ thời gian mượn để đến lấy sách"+
                            "Vui lòng đến thư viện để làm thủ tục nhận sách.\n\n" +
                            "Trân trọng."
            );

            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






}

