package Project1.com.LibraryManagement.Config;

import Project1.com.LibraryManagement.Service.CustomOAuth2UserService;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(2)
public class UserConfig {
    @Autowired
    private final CustomOAuth2UserService customOAuth2UserService;

    public UserConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception{
        return http
                .securityMatcher("/user/**",
                        "/",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/oauth2/**",
                        "/login/oauth2/**",   //
                        "/logout")
                .csrf(csrf-> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/user/home","/user/login","/user/register","/user/saveUsers",
                                "/user/introduce", "/user/forgotPassword","/user/resetPassword/**",
                                "/user/pinbooks","/user/suggest","/user/verify-otp"
                        ).permitAll()

                        .requestMatchers(
                                "/user/announcement","/user/documents",
                                "/user/feedback","/user/lookup","/user/support"
                        ).authenticated()

                        .requestMatchers("/","/css/**","/js/**","/images/**").permitAll()
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/user/login")
                        .loginProcessingUrl("/user/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/user/home",true)
                        .failureUrl("/user/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                                .logoutSuccessUrl("/user/login?logout=true")
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID") //delete session cookie
                )

                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/user/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl("/user/home", true)
                        .permitAll()
                )
//                .oauth2Login(oauth -> oauth
//                        .loginPage("/login")
//                        .defaultSuccessUrl("/home", true)
//                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )
                .rememberMe(remember -> remember
                        .key("someSecretKey")
                        .tokenValiditySeconds(7*24*60*60)
                )
                .build();
    }

}
