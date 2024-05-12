package io.exam.ws.config;
import io.exam.auth.JWTAuth;
import io.exam.auth.UserContext.UserInfo;
import io.exam.errors.AuthenticationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private final JWTAuth jwtAuth;

  public WebSocketConfig(JWTAuth jwtAuth) {
    this.jwtAuth = jwtAuth;
  }



  @Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws").setAllowedOrigins("*");
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.setApplicationDestinationPrefixes("/chat");
		config.enableSimpleBroker("/topic", "/queue");
	}

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
		registry.setMessageSizeLimit(4 * 8192);
		registry.setTimeToFirstMessage(5000);
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor =
						MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

				if (StompCommand.CONNECT.equals(accessor.getCommand())) {
					var tokens = accessor.getMessageHeaders().get("nativeHeaders", LinkedMultiValueMap.class)
							.get(HttpHeaders.AUTHORIZATION);
					if (tokens == null || tokens.isEmpty()) {
						throw new AuthenticationException("UNAUTHORIZED");
					}

					try {
						var payload = jwtAuth.decode(tokens.get(0).toString());
						var userInfo = UserInfo.builder().userId(Long.parseLong(payload.getStringClaim("sub")))
								.username(payload.getStringClaim("username")).build();
						accessor.setUser(userInfo);
					} catch (Exception e) {
						log.error("message err", e);
						throw new AuthenticationException("UNAUTHORIZED");
					}
				}

				return message;
			}
		});
	}

//	@Bean
//	public HandlerMapping handlerMapping() {
//		Map<String, WebSocketHandler> map = new HashMap<>();
//		map.put("/ws", new ChatSocketHandler());
//		int order = -1; // before annotated controllers
//
//		return new SimpleUrlHandlerMapping(map, order);
//	}

}