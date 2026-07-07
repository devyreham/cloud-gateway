package com.devyreham.gateway.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class AccessErrorHandler implements WebFilter {

  public static final String DEFAULT_MSG = "{\"status\":\"403\", \"message\":\"Authorization Bearer missing or invalid\"}";

  private String message;

  public AccessErrorHandler() {
    this.message = DEFAULT_MSG;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    ServerHttpResponse mutatedResponse = exchange.getResponse();
    mutatedResponse.setStatusCode(HttpStatus.FORBIDDEN);
    mutatedResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    mutatedResponse
        .writeWith(Mono.just(mutatedResponse.bufferFactory().wrap(this.message.getBytes())));
    ServerWebExchange mutatedExchange = exchange.mutate().response(mutatedResponse).build();
    return chain.filter(mutatedExchange);
  }
}
