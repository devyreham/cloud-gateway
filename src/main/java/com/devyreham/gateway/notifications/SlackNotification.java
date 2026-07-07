package com.devyreham.gateway.notifications;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.stereotype.Service;

/**
 * send Jira messages
 */
@Service
public class SlackNotification implements Notification {

  private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

  private static final Logger LOG = Logger.getLogger(SlackNotification.class.getName());
  private static final String SLACK_CHANNEL = "channel";
  private static final String SLACK_USERNAME = "username";

  private String webhookurl;

  private String channel;

  private String emoji;

  private String username;

  private CloseableHttpClient httpclient;


  @Override
  public void configure(Map<String, String> params) {
    this.webhookurl = params.get("webhookurl");
    this.channel = params.get(SLACK_CHANNEL);
    this.emoji = params.get("emoji");
    this.username = params.get(SLACK_USERNAME);

    if (httpclient == null) {
      PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
      connectionManager.setValidateAfterInactivity(TimeValue.ofSeconds(5));
      connectionManager.setDefaultMaxPerRoute(20);
      httpclient = HttpClients.custom().disableCookieManagement().disableConnectionState()
          .setConnectionManager(connectionManager).build();
    }
  }

  @Override
  public void send(String content) {
    Map<String, String> params = new HashMap<>();
    params.put("webhookurl",
        "https://hooks.slack.com/services/AAAAA/ABCDE");
    params.put(SLACK_CHANNEL, "#builds");
    params.put("emoji", ":radioactive_sign:");
    params.put(SLACK_USERNAME, "Service status");
    configure(params);
    if (content != null) {
      HttpUriRequestBase httpBase = new HttpPost(this.webhookurl);
      String payload = buildPayload(content);
      httpBase.setEntity(
          new StringEntity(payload, ContentType.create(APPLICATION_X_WWW_FORM_URLENCODED,
              StandardCharsets.UTF_8)));
      try (CloseableHttpResponse response = httpclient.execute(httpBase)) {
        LOG.info("Slack Webhook " + response.getCode());
        HttpEntity entity = response.getEntity();
        EntityUtils.consume(entity);
      } catch (IOException e) {
        LOG.warning(e.toString());
      }
    }
  }

  @Override
  public String getType() {
    return "slack";
  }

  /**
   *  body to be sent from http
   * @param text
   * @return
   */
  String buildPayload(String text) {
    StringBuilder payload = new StringBuilder("payload=");

    ObjectMapper mapper = new ObjectMapper();
    ObjectNode jsonPayload = mapper.createObjectNode();
    jsonPayload.put(SLACK_CHANNEL, this.channel);
    jsonPayload.put(SLACK_USERNAME, this.username);
    jsonPayload.put("icon_emoji", this.emoji);
    try {
      jsonPayload.put("text", URLEncoder.encode(text, StandardCharsets.UTF_8.toString()));
    } catch (UnsupportedEncodingException e) {
      jsonPayload.put("text", text);
    }

    payload.append(jsonPayload);
    return payload.toString();
  }

}
