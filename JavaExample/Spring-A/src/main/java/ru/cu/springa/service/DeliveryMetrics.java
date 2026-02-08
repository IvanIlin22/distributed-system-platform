package ru.cu.springa.service;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMetrics {

  private final LongCounter deliveryMessagesSentCounter;

  private static final AttributeKey<String> MESSAGE_ID_KEY = AttributeKey.stringKey("message_id");
  private static final String SERVICE_NAME = "delivery-sender";
  private static final String COUNTER_NAME = "delivery_messages_sent_total";


  public DeliveryMetrics(OpenTelemetry openTelemetry) {
    Meter meter = openTelemetry.getMeter(SERVICE_NAME);

    this.deliveryMessagesSentCounter = meter.counterBuilder(COUNTER_NAME)
        .setDescription("Total number of delivery messages sent")
        .setUnit("messages")
        .build();
  }

  public void incrementDeliveryMessageSent(String messageId) {
    deliveryMessagesSentCounter.add(1, Attributes.of(MESSAGE_ID_KEY, messageId));
  }
}
