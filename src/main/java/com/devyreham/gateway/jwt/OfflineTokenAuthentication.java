package com.devyreham.gateway.jwt;

import tools.jackson.core.json.JsonFactory;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Authentication object to pass to spring security filter chain
 */
public class OfflineTokenAuthentication implements Authentication {

  /**
   * map of claims
   */
  private final transient Map<String, Object> tokenClaims;

  /**
   * always true we assume token is from authentication
   */
  private boolean authenticated;

  public OfflineTokenAuthentication() {
    this(null);
  }

  public OfflineTokenAuthentication(Map<String, Object> tokenClaims) {
    this.tokenClaims = tokenClaims;
    this.authenticated = true;
  }

  /**
   * retrieve roles from token. Works only if resource_access exits
   * getAuthorities cannot be null or it will throw an exception
   * @return
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> authorities = new ArrayList<>();
    if (this.tokenClaims == null) {
      return authorities;
    }
    try {
      JsonFactory jfact = JsonFactory.builder().enable(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES).build();
      ObjectMapper objectMapper = JsonMapper.builder(jfact).build();

      JsonNode jsonNode = objectMapper
          .readValue(this.tokenClaims.get("resource_access").toString(), JsonNode.class);

      /* identify role by clients */
      Object clientApp = this.tokenClaims.get("azp");
      String azpField = clientApp == null ? null : clientApp.toString();
      if (azpField != null && jsonNode.get(azpField) != null) {

        List<JsonNode> roleArray = jsonNode.get(azpField).findValues("roles");

        /* each role nodes is an array, we need to convert them to a single Authorities */
        for (JsonNode roleNode : roleArray) {
          roleNode.forEach( iter -> {authorities.add(new SimpleGrantedAuthority(iter.asString()));});
        }
      }

      return authorities;
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return authorities;
    }
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getDetails() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    /* we should not have a null principal */
    return this.tokenClaims == null ? null : this.tokenClaims.get("sub");
  }

  @Override
  public boolean isAuthenticated() {
    return authenticated;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    authenticated = isAuthenticated;
  }

  @Override
  public String getName() {
    /* some token do not have name, we must prevent null here*/
    if (this.tokenClaims == null) {
      return "";
    } else {
      return this.tokenClaims.get("email") == null ? preventNull(
          this.tokenClaims.get("preferred_username")) : preventNull(this.tokenClaims.get("email"));
    }
  }

  /**
   * force toString to be something
   * @param input
   * @return
   */
  private String preventNull(Object input) {
    return input == null ? "" : input.toString();
  }

}
