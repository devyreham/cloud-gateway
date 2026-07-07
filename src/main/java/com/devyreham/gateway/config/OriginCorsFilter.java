package com.devyreham.gateway.config;

import java.util.Collections;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * alternative cors filter based on spring boot CorsFilter. It sends the real origin instead fo *
 */
public class OriginCorsFilter implements WebFilter {


  public OriginCorsFilter() {
    /* empty for spring dependency injection*/
  }

  /**
   * This filter put the header Access-Control-Allow-Origin: realorigin instead of wildcard
   * According this issue below, you cannot disable default @crossorigin headers
   * unless you put the cors header here before
   * https://github.com/spring-projects/spring-framework/issues/18266
   *
   * @param exchange
   * @param filterChain
   * @return
   */
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain filterChain) {
    ServerHttpRequest request = exchange.getRequest();
    boolean isPreflight = (HttpMethod.OPTIONS == request.getMethod()) &&
        (request.getHeaders().getOrigin() != null) &&
        (request.getHeaders().getAccessControlRequestMethod() != null);

    if (isPreflight) {
      exchange.getResponse().getHeaders().setAccessControlAllowHeaders(exchange.getRequest().getHeaders()
          .getAccessControlRequestHeaders());
        exchange.getResponse().getHeaders()
            .setAccessControlAllowMethods(Collections
                .singletonList(exchange.getRequest().getHeaders().getAccessControlRequestMethod()));
    }

    if (request.getHeaders().getOrigin() != null) {
      String origin = request.getHeaders().getOrigin();
      exchange.getResponse().getHeaders().setAccessControlAllowOrigin(origin);
      exchange.getResponse().getHeaders().setAccessControlMaxAge(600);
      exchange.getResponse().getHeaders()
          .setAccessControlExposeHeaders(Collections.singletonList("Access-Control-Allow-Methods"));
    }

    return filterChain.filter(exchange);
  }
}
