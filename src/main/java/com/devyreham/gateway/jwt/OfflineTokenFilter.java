package com.devyreham.gateway.jwt;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import com.devyreham.gateway.config.GatewayConfiguration;
import reactor.core.publisher.Mono;

public class OfflineTokenFilter implements WebFilter {

  private static final String X_REAL_IP = "X-Real-IP";
  private static final Logger LOG = Logger.getLogger(OfflineTokenFilter.class.getName());

  private GatewayConfiguration properties;

  private JwtTokenReader tokenReader;

  private final AccessErrorHandler errorHandler = new AccessErrorHandler();

  public OfflineTokenFilter(GatewayConfiguration properties) {
    this.properties = properties;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain filterChain) {
    ServerHttpRequest request = exchange.getRequest();
    /* in case this filter catch OPTIONS*/
    if (request.getMethod() == HttpMethod.OPTIONS) {
      return filterChain.filter(exchange);
    }
    /* Allow services who cannot have token */
    if (isAllowedService(request)) {
      TokenBypassAuthentication bypass = new TokenBypassAuthentication();
      ReactiveSecurityContextHolder.withAuthentication(bypass);

      return filterChain.filter(exchange);
    }

    List<String> authHeader = request.getHeaders().getOrEmpty(JwtTokenReader.AUTH_HEADER);
    if (authHeader.isEmpty()) {

      /* do not process*/
      String errorMsg = "Expected a token but not present " + request.getPath();
      LOG.warning(errorMsg);
      LOG.log(Level.WARNING, () -> request.getHeaders().toString());
      return errorHandler.filter(exchange, filterChain);
    } else {
      String auth = authHeader.get(0);
      /* semi validate auth header */
      return processAuthorizationHeader(auth, exchange, filterChain);
    }
  }

  /**
   *
   * @param auth
   * @param exchange
   * @param filterChain
   */
  private Mono<Void> processAuthorizationHeader(String auth, ServerWebExchange exchange,
                                                WebFilterChain filterChain) {
    if (this.tokenReader == null) {
      this.tokenReader = new JwtTokenReader();
    }
    /* handle Bearer only */
    if (!auth.matches("\\s*Bearer\\s.*")) {
      String errorMsg = "Header unknown or not supported " + exchange.getRequest().getPath();
      LOG.warning(errorMsg);
      return errorHandler.filter(exchange, filterChain);
    }

    try {
      String tokenString = auth.substring(auth.indexOf(JwtTokenReader.BEARER) + 7);

      if (this.tokenReader
              .validate(tokenString, properties.getRealms(), properties.getClients())) {
        Map<String, Object> mapClaims = this.tokenReader.parse(tokenString);
        OfflineTokenAuthentication authentication = new OfflineTokenAuthentication(mapClaims);
        ReactiveSecurityContextHolder.withAuthentication(authentication);
        boolean hasAccess = mapClaims.get("resource_access").toString().contains("ROLE_ACCESS")
                || mapClaims.get("resource_access").toString().contains("ROLE_USER");

        if (hasAccess) {
          return filterChain.filter(exchange);
        } else {
          LOG.info("No Acesss " + mapClaims.get("resource_access").toString());
          return errorHandler.filter(exchange, filterChain);
        }

      } else {
        String errorMsg = "Token failed validation " + exchange.getRequest().getPath();
        LOG.warning(errorMsg);
        return errorHandler.filter(exchange, filterChain);
      }
    } catch (Exception e) {
      LOG.warning(e.getMessage());
      return errorHandler.filter(exchange, filterChain);
    }
  }

  /**
   * allow already authentified backend services and internal IP
   *
   * @param request
   * @return
   */
  boolean isAllowedService(ServerHttpRequest request) {
    String[] bypassers = {"/js","/css", "/about", "/cdn"};
    Optional<String> found = Arrays.stream(bypassers)
            .filter(e -> request.getPath().toString().contains(e)).findAny();

    if (found.isPresent()) {
      LOG.log(Level.INFO, () -> "Bypass security " + request.getPath());
      return true;
    } else {
      List<String> realIpList = request.getHeaders().getOrEmpty(X_REAL_IP);
      String realIp = realIpList.isEmpty() ? StringUtils.EMPTY : realIpList.get(0);

      boolean allowed = Arrays.stream(properties.getAllips())
              .anyMatch(ip -> ip.equalsIgnoreCase(realIp));

      if (allowed) {
        LOG.log(Level.INFO, () -> X_REAL_IP + " " + realIp + " " + request.getPath());
        LOG.log(Level.INFO, () -> request.getHeaders().toString());
      }
      return allowed;
    }

  }

}
