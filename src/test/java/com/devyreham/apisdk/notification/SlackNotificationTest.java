package com.devyreham.apisdk.notification;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.devyreham.gateway.notifications.SlackNotification;

class SlackNotificationTest {

  SlackNotification notification;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void send() {
    notification = new SlackNotification();
    assertNotNull(notification);
    Map<String, String> params = new HashMap<>();
    params.put("webhookurl",
        "https://hooks.slack.com/services/ABCD/EFGH");
    params.put("channel", "#builds");
    params.put("emoji", ":pray:");
    params.put("username", "unit test");
    notification.configure(params);
    //notification.send("testing build");
  }
}