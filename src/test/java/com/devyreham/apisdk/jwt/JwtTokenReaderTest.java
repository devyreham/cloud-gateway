package com.devyreham.apisdk.jwt;

import static org.junit.jupiter.api.Assertions.assertFalse;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.devyreham.gateway.jwt.JwtTokenReader;


class JwtTokenReaderTest {

  @Test
  void parse() {

    String accessToken = "";
    JwtTokenReader tokenReader = new JwtTokenReader();
    try {
      Map<String, Object> mapClaims = tokenReader.parse(accessToken);
      for (String key : mapClaims.keySet()) {
        System.out.println(
            key + ": " + mapClaims.get(key) + " type " + mapClaims.get(key).getClass().getName());
      }
      boolean isValid = tokenReader.validate(accessToken, "Realm1,Realm2",
          "oid-services,openid-client");
      assertFalse(isValid);
      isValid = tokenReader.validate(accessToken, "Realm1,Realm2",
          "oid-services");
      assertFalse(isValid);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void rejectModifiedToken() {

    String accessToken = "";
    JwtTokenReader tokenReader = new JwtTokenReader();
    try {
      Map<String, Object> mapClaims = tokenReader.parse(accessToken);
      for (String key : mapClaims.keySet()) {
        System.out.println(
            key + ": " + mapClaims.get(key) + " type " + mapClaims.get(key).getClass().getName());
      }
      assertFalse(tokenReader.validate(accessToken, "EAAS", "souscription"));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}