package com.devyreham.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.session.WebSessionManager;
import com.devyreham.gateway.jwt.OfflineTokenFilter;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

  @Bean
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return new NullAuthenticatedSessionStrategy();
  }

  @Bean
  @Order(-200)
  protected OfflineTokenFilter tokenFilter(GatewayConfiguration khresterionConfiguration) {
    return new OfflineTokenFilter(khresterionConfiguration);
  }

  /**
   * override @crossorigin
   * @return
   */
  @Bean
  OriginCorsFilter corsFilter() {
    return new OriginCorsFilter();
  }


  @Bean
  public WebSessionManager webSessionManager() {
    // Emulate SessionCreationPolicy.STATELESS
    return exchange -> Mono.empty();
  }

  @Bean
  SecurityWebFilterChain apiHttpSecurity(ServerHttpSecurity http) {
    http.authorizeExchange(exchange -> exchange.anyExchange().permitAll())
        .csrf(ServerHttpSecurity.CsrfSpec::disable);
    return http.build();
  }
}
