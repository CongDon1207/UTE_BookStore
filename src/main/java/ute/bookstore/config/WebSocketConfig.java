package ute.bookstore.config;
import org.springframework.context.annotation.Configuration; // Đánh dấu class là cấu hình
import org.springframework.messaging.simp.config.MessageBrokerRegistry; // Cấu hình message broker
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker; // Kích hoạt message broker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry; // Đăng ký STOMP endpoints
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer; // Interface cấu hình WebSocket



@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	@Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

	@Override 
	public void registerStompEndpoints(StompEndpointRegistry registry) {
	    registry.addEndpoint("/ws/chat")
	        .setAllowedOriginPatterns("http://localhost:*")  // Thay vì "*"
	        .withSockJS();
	}
}