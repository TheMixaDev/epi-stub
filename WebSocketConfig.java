@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Префикс для сообщений от сервера к клиенту
        config.enableSimpleBroker("/topic", "/queue");
        
        // Префикс для сообщений от клиента к серверу
        config.setApplicationDestinationPrefixes("/app");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket endpoint
        registry.addEndpoint("/ws-compile")
            .setAllowedOriginPatterns("*")
            .withSockJS();
    }
}
