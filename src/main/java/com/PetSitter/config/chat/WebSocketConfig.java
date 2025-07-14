package com.PetSitter.config.chat;

import com.PetSitter.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // STOMP 기반 WebSocket 메시징을 활성화
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final TokenProvider tokenProvider;

    // 메시지를 어떤 경로로 보낼지 결정 (/app, /topic, /queue, /user 등)
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지를 받을 수 있는 목적지(prefix)
        config.enableSimpleBroker("/topic", "/queue");  // 메시지 브로커(서버가 바로 클라이언트에 직빵으로 전달하는 것이 아닌 중간에 메시지 브로커가 껴서 전달)가 클라이언트에게 메시지를 전달할 수 있도록 허용하는 prefix. /queue는 1:1, /topic은 1:N에 자주 사용.(서버 -> 클라이언트 보낼 때 경로)
        config.setApplicationDestinationPrefixes("/app");   // 클라이언트가 메시지를 보낼 때 붙이는 prefix. @MessageMapping은 이 prefix 기준으로 매핑.(클라이언트 -> 서버 보낼 때 경로)
        config.setUserDestinationPrefix("/user");   // 1:1 통신 시 사용, 특정 사용자에게 메시지를 보낼 때 사용하는 prefix. 서버에서 @SendToUser("/queue/xxx") -> 클라이언트는 /user/queue/xxx로 구독해야 함.(@SendToUser를 위한 prefix)
    }

    // 클라이언트가 WebSocket 연결을 맺을 때 사용할 엔드포인트를 등록
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트에서 WebSocket 연결을 위해 사용할 엔드포인트
        registry.addEndpoint("/ws-chat") // ex: ws://localhost:8080/ws-chat, WebSocket 연결을 시작할 때 사용할 엔드포인트. 클라이언트는 이 경로로 3-handshake를 시도.
                .setAllowedOriginPatterns("*") // CORS 허용
                .addInterceptors(new UserHandshakeInterceptor(tokenProvider))
                .withSockJS(); // SockJS fallback 지원, WebSocket을 사용할 수 없는 환경에서 fallback으로 SockJS 사용 가능하게 함.
    }

    // 클라이언트가 보낸 메시지를 가로채기 위한 인터셉터 등록 (ex: 토큰 확인 등)
    // SecurityContextHolder는 ThreadLocal 기반(로직을 수행하는 특정 스레드에서만 값 저장 -> 값은 같은 스레드 내에서만 공유하게 됨.)
    // WebSocket 처리 중엔 비동기 처리나 다른 스레드에서 동작할 수 있기 때문에 SecurityContextHolder가 공유되지 않음.
    // 그래서 매 요청마다 SecurityContextHoler.setContext()로 명시적으로 인증 정보를 설정해 줘야 함.
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {    // ChannelInterceptor는 메시지 채널을 가로채서 인증 처리, 로깅, 필터링 등을 할 수 있는 인터페이스.
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                // 1. 소켓 연결 시작 시 헤더에 토큰이 있는 경우
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Authorization");

                    // 2. 소셜 로그인 사용자 (JWT 기반)
                    if (token != null && token.startsWith("Bearer ")) {
                        String jwt = token.substring(7);
                        Authentication auth = tokenProvider.getAuthentication(jwt);
                        accessor.setUser(auth); // Websocket에 인증 정보 설정(Principal용 설정)

                        // SecurityContextHolder에도 명시적으로 설정
                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        context.setAuthentication(auth);
                        SecurityContextHolder.setContext(context);

                        return message;
                    }

                    // 3. 세션 기반 사용자 (폼 로그인, OAuth2 로그인)
                    Authentication auth = (Authentication) accessor.getSessionAttributes().get("user");

                    // 세션에서 못 가져왔을 때 스프링 컨텍스트 홀더에서 직접 인증 객체를 가져오기
                    if (auth == null) {
                        auth = SecurityContextHolder.getContext().getAuthentication();
                    }

                    // 4. 폼 로그인 사용자
                    log.info("WebSocket Authentication info={}", auth);
                    if (auth != null && auth.isAuthenticated()) {
                        System.out.println("hi");
                        accessor.setUser(auth);

                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        context.setAuthentication(auth);
                        SecurityContextHolder.setContext(context);
                    }
                }
                return message;
            }
        });
    }
}
