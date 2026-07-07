package com.devyreham.apisdk.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import com.devyreham.gateway.config.GatewayConfiguration;

@SpringBootTest
@ActiveProfiles("dev")
class GatewayConfigurationTest {

  @Autowired
  GatewayConfiguration config;

  @Autowired
  ApplicationContext context;

  @Test
  void testReload() {

    assertEquals("EAAS,ISACS,Souscription,DEMO", config.getRealms());
    assertTrue(config.getClients().contains("ontotext"));
    assertNotNull(context.getBean("corsFilter"));
    assertNotNull(context.getBean("tokenFilter"));
    System.out.println("Hello");
    for(String p:context.getEnvironment().getActiveProfiles()){
      System.out.println(p);
    }

    assertTrue(Arrays.asList(context.getEnvironment().getActiveProfiles()).contains("dev"));
  }
}