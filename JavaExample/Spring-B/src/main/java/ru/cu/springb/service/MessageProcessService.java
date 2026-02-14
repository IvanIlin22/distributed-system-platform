package ru.cu.springb.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageProcessService {

  private static final Map<String, Boolean> MESSAGE_ID = new ConcurrentHashMap<>();

  public static Boolean processMessage(String messageId) {
    return MESSAGE_ID.putIfAbsent(messageId, true);
  }
}
