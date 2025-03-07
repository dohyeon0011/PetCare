package com.PetSitter.config;

import com.PetSitter.service.Member.MemberDetailsService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final MemberDetailsService memberDetailsService;

    // 스프링 시큐리티가 활성화된 상태에서는 로그인과 로그아웃 URL에 대한 기본 처리가 자동으로 이루어지므로, 별도로 컨트롤러를 구현하지 않아도 됨.
    // 커스텀 로그인 페이지나 추가적인 로직이 필요한 경우, 그때 추가적인 설정을 하거나 필터를 구현
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // 로그인 시 세션 생성
                )
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/pets-care/login", "/pets-care/signup", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/pets-care/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/pets-care/main", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")   // 로그아웃 URL
                        .logoutSuccessUrl("/pets-care/main")
                        .invalidateHttpSession(true)   // 세션 무효화
                        .deleteCookies("JSESSIONID")   // 쿠키 삭제
                        .permitAll()
                );

        return http.build();
    }

    // Spring Security 6.1 이후에는 AuthenticationManager 설정을 더 이상 .and()로 체이닝하지 않고 AuthenticationManagerBuilder를 직접 사용해야 함.
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder,
                                                       MemberDetailsService memberDetailsService) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(memberDetailsService)
                .passwordEncoder(bCryptPasswordEncoder);

        return authenticationManagerBuilder.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { // 구체적인 클래스 타입을 BCryptPasswordEncoder로 지정하지 않아도 PasswordEncoder 인터페이스(임의의 이름으로 작명)로 자동으로 다형성을 지원하기 때문에 동일하게 작동
        return new BCryptPasswordEncoder();
    }
}
