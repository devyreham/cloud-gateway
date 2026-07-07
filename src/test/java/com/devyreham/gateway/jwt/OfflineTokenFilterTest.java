package com.devyreham.gateway.jwt;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.WebFilterChain;
import com.devyreham.gateway.jwt.OfflineTokenFilter;
import reactor.core.publisher.Mono;

@SpringBootTest
@ActiveProfiles("dev")
class OfflineTokenFilterTest {

  @Autowired
  OfflineTokenFilter filter;

  @Test
  void isAllowedService() {

    MockServerHttpRequest request = MockServerHttpRequest.get("http://127.0.0.1/banking/payment")
        .header("X-Real-IP", "10.0.0.2")
        .header("User-Agent", "curl").build();
    assertTrue(filter.isAllowedService(request));

    MockServerHttpRequest bypassreq = MockServerHttpRequest.get("http://127.0.0.1/schema/dl")
        .header("X-Real-IP", "10.0.0.2")
        .header("User-Agent", "curl").build();
    assertTrue(filter.isAllowedService(bypassreq));

    MockServerHttpRequest getSousbackv2 = MockServerHttpRequest.get("http://127.0.0.1/cryptobackend/")
        .header("X-Real-IP", "10.0.0.2")
        .header("Authorization", "Bearer toto").build();
    assertTrue(filter.isAllowedService(getSousbackv2));

    MockServerHttpRequest getFfrBack = MockServerHttpRequest.get("http://127.0.0.1/backend/")
        .header("X-Real-IP", "10.0.0.2")
        .header("Authorization", "Bearer toto").build();
    assertTrue(filter.isAllowedService(getFfrBack));

    MockServerHttpRequest getAdminservices = MockServerHttpRequest.get("http://127.0.0.1/adminservices/")
        .header("X-Real-IP", "10.0.0.2")
        .header("Authorization", "Bearer toto").build();
    assertTrue(filter.isAllowedService(getAdminservices));

    MockServerHttpRequest wrongrequest = MockServerHttpRequest.get("http://127.0.0.1/toto")
        .header("X-Real-IP", "127.0.0.1")
        .header("User-Agent", "curl").build();
    assertFalse(filter.isAllowedService(wrongrequest));

    MockServerHttpRequest pyrequest = MockServerHttpRequest.get("http://127.0.0.1")
        .header("User-Agent", "python-requests","Java,HttpClient","Postman")
        .header("X-Real-IP", "149.202.163.63")
        .build();
    assertFalse(filter.isAllowedService(pyrequest));

    String accessToken = "";

    MockServerHttpRequest authRequest = MockServerHttpRequest
        .get("https://dev.devyreham.com/gemini/run")
        .header("Authorization", "Bearer " + accessToken).header("X-Real-IP", "5.37.6.2")
        .header("Host","dev.devyreham.com")
        .build();
    MockServerWebExchange exchange = MockServerWebExchange.builder(authRequest).build();
    WebFilterChain filterChain = filterExchange -> Mono.empty();
    assertNotNull(filter.filter(exchange, filterChain));

    /* send wrong header */
    authRequest = MockServerHttpRequest
        .get("https://dev.devyreham.com/happyhorse/validate")
        .header("Authorization", "Basic 1234").header("X-Real-IP", "5.37.6.2")
        .header("Host","dev.devyreham.com")
        .build();
    exchange = MockServerWebExchange.builder(authRequest).build();
    filterChain = filterExchange -> Mono.empty();
    assertNotNull(filter.filter(exchange, filterChain));
  }
}