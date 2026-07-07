package com.devyreham.gateway.jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * virtual authentication for services with all required roles
 */
public class TokenBypassAuthentication implements Authentication {

  private static final String[] defaultRoles = {"ROLE_API", "ROLE_ACCESS"};
  private static final String SERVICE_NAME = "oid-services";

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Arrays.stream(defaultRoles).map(SimpleGrantedAuthority::new).collect(
        Collectors.toList());
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
    return SERVICE_NAME;
  }

  @Override
  public boolean isAuthenticated() {
    return true;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    /* empty for spring dependency injection*/
  }

  @Override
  public String getName() {
    return getPrincipal().toString();
  }
}
