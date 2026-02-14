package ru.cu.springb.service;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import org.springframework.stereotype.Service;

@Service
public class DeliveryReceiver {

  private final LongCounter deliveryMessagesReceivedCounter;

  private static final AttributeKey<String> MESSAGE_ID_KEY = AttributeKey.stringKey("message_id");
  private static final String SERVICE_NAME = "delivery-receiver";
  private static final String COUNTER_NAME = "delivery_messages_received_total";

  public DeliveryReceiver(OpenTelemetry openTelemetry) {
    Meter meter = openTelemetry.getMeter(SERVICE_NAME);

    this.deliveryMessagesReceivedCounter = meter.counterBuilder(COUNTER_NAME)
        .setDescription("Total number of receiver messages sent")
        .setUnit("messages")
        .build();
  }

  public void incrementDeliveryMessageSent(String messageId) {
    deliveryMessagesReceivedCounter.add(1, Attributes.of(MESSAGE_ID_KEY, messageId));
  }

}
