package co.whitetree.springredisson.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ChatRoomSocketConfig {
    private final WebSocketHandler webSocketHandler; // chatRoomService

    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, WebSocketHandler> map = Map.of("/chat", webSocketHandler);
        return new SimpleUrlHandlerMapping(map, -1); // 우선순위 (precedence)
    }
}
