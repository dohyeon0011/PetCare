package com.PetSitter.config;

import com.PetSitter.config.oauth.CustomOAuth2UserService;
import com.PetSitter.service.Member.MemberDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final MemberDetailsService memberDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;

    // 스프링 시큐리티가 활성화된 상태에서는 로그인과 로그아웃 URL에 대한 기본 처리가 자동으로 이루어지므로, 별도로 컨트롤러를 구현하지 않아도 됨.
    // 커스텀 로그인 페이지나 추가적인 로직이 필요한 경우, 그때 추가적인 설정을 하거나 필터를 구현
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")  // Swagger 관련 경로 허용
                        .permitAll()  // 모든 사용자가 접근 가능하게 허용
                        .requestMatchers("/pets-care/main", "/pets-care/login", "/pets-care/signup",
                                "/pets-care/sitter", "/pets-care/cat-care", "/pets-care/sitters-information",
                                "/pets-care/pet-sitter/information", "/pets-care/reservable-list",
                                "/pets-care/reservable/members/**", "/pets-care/reviews",
                                "/css/**", "/js/**", "/images/**", "/uploads/profile/**",
                                "/uploads/pets/**", "/uploads/carelogs/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pets-care/members/new").permitAll() // 회원가입 API 허용
                        .requestMatchers("/api/pets-care/reservable/members/**").permitAll() // 리뷰 API 추가 (비로그인 상태에서도 접근 가능)
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/pets-care/login")
                        .loginProcessingUrl("/login")
                        .failureHandler(((request, response, exception) -> { // 로그인 정보 불일치 시
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");

                            String message = "아이디 또는 비밀번호가 잘못되었습니다.";

                            if (exception instanceof org.springframework.security.authentication.DisabledException) {
                                message = "회원 탈퇴한 계정입니다.";
                            }

                            response.getWriter().write("{\"message\": \"" + message + "\"}");
                        }))
                        .defaultSuccessUrl("/pets-care/main", true)
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/pets-care/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl("/pets-care/main", true)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/pets-care/main")
                        .invalidateHttpSession(true)  // 세션 무효화
                        .deleteCookies("JSESSIONID") // 쿠키 삭제
                        .clearAuthentication(true)
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
