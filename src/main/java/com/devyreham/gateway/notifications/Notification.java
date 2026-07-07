package com.devyreham.gateway.notifications;

import java.util.Map;

public interface Notification {

  void configure(Map<String, String> params);

  void send(String content);

  String getType();

}