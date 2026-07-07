package com.devyreham.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webflux.autoconfigure.error.ErrorWebFluxAutoConfiguration;
import com.devyreham.gateway.config.GatewayConfiguration;

@SpringBootApplication(exclude = ErrorWebFluxAutoConfiguration.class)
@EnableConfigurationProperties(GatewayConfiguration.class)
public class GatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(GatewayApplication.class, args);
  }

}
