package com.devyreham.gateway.config;

import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.AbstractEnvironment;

/**
 * only the properties prefixed devyreham is managed here. It should prevent reloading other settings
 * like port number, context path,...
 */
@ConfigurationProperties(prefix = "devyreham")
public class GatewayConfiguration {

  private AbstractEnvironment env;
  private String realms;
  private String clients;
  private String defaultdataset;
  private String[] allips;

  @Autowired
  public GatewayConfiguration(AbstractEnvironment env) {
    this.env = env;
  }

  public String getRealms() {
    return realms;
  }

  public void setRealms(String realms) {
    this.realms = realms;
  }

  public String getClients() {
    return clients;
  }

  public void setClients(String clients) {
    this.clients = clients;
  }

  public String getDefaultdataset() {
    return defaultdataset;
  }

  public void setDefaultdataset(String defaultdataset) {
    this.defaultdataset = defaultdataset;
  }

  public Map<String,String> getServices() {
    return Collections.emptyMap();
  }

  public String[] getAllips() {
    return allips;
  }

  public void setAllips(String[] allips) {
    this.allips = allips;
  }
}
