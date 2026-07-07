package com.devyreham.gateway.jwt;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;
import com.devyreham.gateway.jwt.JwtTokenReader;
import com.devyreham.gateway.jwt.OfflineTokenAuthentication;

class OfflineTokenAuthenticationTest {

  @Test
  void getAuthorities() {

    try {
      JwtTokenReader tokenReader = new JwtTokenReader();
      /* test bogus auth*/
      OfflineTokenAuthentication bogusAuth = new OfflineTokenAuthentication();
      assertTrue(bogusAuth.getAuthorities().isEmpty());
      assertTrue(bogusAuth.getName().isEmpty());

      String accessToken = "";
      Map<String, Object> mapClaims = tokenReader.parse(accessToken);

      OfflineTokenAuthentication auth = new OfflineTokenAuthentication(mapClaims);
      assertFalse(auth.getAuthorities().isEmpty());
      assertTrue(auth.isAuthenticated());
      System.out.println(auth.getAuthorities());
      System.out.println(auth.getName());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}