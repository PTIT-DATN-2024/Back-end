package selling_electronic_devices.back_end.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Config prefix
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/notification");// prefix: server pub
        config.setApplicationDestinationPrefixes("/publish");// prefix: app (client) pub
    }

    // Register Endpoint
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat-box").setAllowedOrigins("*");//.withSockJS(); // withSockJS() cung cấp các giải pháp thay thế (fallback) để hỗ trợ kết nối websocket trong những môi trường "ko hỗ trợ websocket thuần"(vd như trình duyệt cũ hoặc server ko hỗ trợ), bỏ withSockJS() -> chúng ta đang dùng WebSocket thuần (native WebSocket) thay vì SockJS
    }
}
