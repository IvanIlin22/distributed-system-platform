package ru.cu.springa.controller;

import java.time.Duration;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import ru.cu.springa.service.DeliveryMetrics;

@RestController
@RequestMapping("/api")
public class MessageController {

  private final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);
  private final DeliveryMetrics deliveryMetrics;
  private final WebClient webClient;

  public MessageController(
      DeliveryMetrics deliveryMetrics,
      WebClient webClient
  ) {
    this.deliveryMetrics = deliveryMetrics;
    this.webClient = webClient;
  }

  @PostMapping("/message-a")
  public ResponseEntity<String> messageA() {
    String messageId = UUID.randomUUID().toString();
    deliveryMetrics.incrementDeliveryMessageSent(messageId);
    LOGGER.info("Message A: {}", messageId);

    try {
      webClient.post()
          .uri("/api/message-b")
          .bodyValue(messageId)
          .retrieve()
          .toEntity(String.class)
          .timeout(Duration.ofSeconds(1))
          .block();
    } catch (Exception e) {
      LOGGER.error("Error while sending a message {}", e.getClass());
    }

    return ResponseEntity.ok("Message sent with ID: " + messageId);
  }

  @PostMapping("/error")
  public ResponseEntity<Void> error() {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
