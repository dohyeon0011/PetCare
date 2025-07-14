/*
package com.PetSitter.config.chat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurity {
    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        return messages
                .simpTypeMatchers(SimpMessageType.CONNECT).authenticated()       // 연결 시 인증 필수
                .simpDestMatchers("/app/**").authenticated() // 클라이언트 -> 서버 전송 시
                .simpSubscribeDestMatchers("/user/**", "/queue/**").authenticated() // 메시지 브로커가 클라이언트에게 전송할 때
                .simpTypeMatchers(SimpMessageType.MESSAGE, SimpMessageType.SUBSCRIBE).authenticated() // 메시지 송수신, 구독도 인증 필수
                .anyMessage().denyAll()
                .build();
    }
}
*/
