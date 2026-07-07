package com.devyreham.gateway.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.core.io.ClassPathResource;

public class JwtTokenReader {

  static final String BEARER = "Bearer";
  static final String BASIC = "Basic";
  static final String AUTH_HEADER = "Authorization";
  private static final Logger LOG = Logger.getLogger(JwtTokenReader.class.getName());
  private static final String SEP = ",";

  /**
   * parse token and prevent error to bubble
   * @param accessToken
   * @return the token claims or empty map
   * @throws ParseException
   */
  public Map<String, Object> parse(String accessToken) throws ParseException {
    JWT jwt = JWTParser.parse(accessToken);

    return jwt.getJWTClaimsSet().getClaims();
  }

  /**
   * many fields like typ,azp,resource_access fields are not standards, see JWTClaimsSet
   * they are only available from Keycloak SSO.
   *
   * @param accessToken token claims
   * @param realmList authorized realms
   * @param appClientList authorized clients
   * @return
   */
  public boolean validate(String accessToken, String realmList, String appClientList) {

    try {
      JWT jwt = JWTParser.parse(accessToken);
      Map<String, Object> mapClaims = jwt.getJWTClaimsSet().getClaims();
      /* skip if any mandatory fields are missing */
      if (mapClaims.isEmpty() || mapClaims.get("iss") == null || mapClaims.get("exp") == null
          || mapClaims.get("azp") == null) {
        LOG.info("Bogus token received");
        return false;
      }
      /* token type must be Bearer, double check to prevent simple forged token*/
      boolean isValid = mapClaims.get("typ").toString().equals(BEARER);

      if (isValid) {
        /* client must match existing one */
        String[] allClients = appClientList.split(SEP);
        String finalApp = mapClaims.get("azp").toString();
        isValid = Arrays.asList(allClients).contains(finalApp);
      }

      if (isValid) {
        /* issuer format can be an url or a text */
        String issuer = mapClaims.get("iss").toString();
        int indexRealm = issuer.lastIndexOf("/");
        String finalIssuer = indexRealm > 0 ? issuer.substring(indexRealm + 1) : issuer;
        String[] allRealms = realmList.split(SEP);
        isValid = Arrays.asList(allRealms).contains(finalIssuer);

        /* verify signature */
        String keypath = "keys/dev_openid.json";

        isValid = isValid && verifySignature(jwt, keypath);
      }

      if (isValid) {
        /* check expiration date tolerate 10min */
        Date expireDate = (Date) mapClaims.get("exp");
        isValid = Instant.now().minusSeconds(600)
            .isBefore(Instant.ofEpochMilli(expireDate.getTime()));
      }

      StringBuilder userInfo = new StringBuilder("User: ");
      LOG.log(Level.FINE, () -> userInfo.append(mapClaims.get("sub")).append(" ")
          .append(mapClaims.get("preferred_username")).toString());

      return isValid;

    } catch (Exception e) {
      /* prevent errors for bubbling */
      LOG.info(e.getMessage());
      return false;
    }
  }

  /**
   * verify token integrity
   * @param jwt
   * @param keypath
   * @return
   * @throws JOSEException
   */
  private boolean verifySignature(JWT jwt, String keypath) throws JOSEException {

    try (InputStream input = new ClassPathResource(keypath).getInputStream()) {
      String jwks = new String(input.readAllBytes(), StandardCharsets.UTF_8);
      JWSVerifier jwsVerifier = new RSASSAVerifier(RSAKey.parse(jwks));

      /* ideally it will throw a illegal type exception if not signed */
      boolean isSigned = ((SignedJWT) jwt).verify(jwsVerifier);
      if (!isSigned) {
        LOG.warning("Signed but compromised token");
      }
      return isSigned;
    } catch (IOException | ParseException e) {
      LOG.warning(e.getMessage());
      return false;
    }
  }
}
