package udpm.hn.server.infrastructure.config.websocket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${frontend.url}")
    public String ALLOWED_ORIGIN;

    @Value("${ws.applicationPrefix}")
    private String applicationPrefix;

    @Value("${ws.topicPrefix}")
    private String topicPrefix;

    @Value("${ws.registerEndpoint}")
    private String registerEndpoint;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(topicPrefix);
        registry.setApplicationDestinationPrefixes(applicationPrefix);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint(registerEndpoint)
                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins(ALLOWED_ORIGIN)
                .withSockJS();
    }

}
