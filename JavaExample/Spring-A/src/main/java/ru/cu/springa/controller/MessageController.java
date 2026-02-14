package ru.cu.springa.controller;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import ru.cu.springa.service.DeliveryMetrics;

@RestController
@RequestMapping("/api")
public class MessageController {

  private static final ThreadLocal<String> IDEMPOTENCY_KEY_HOLDER = new ThreadLocal<>();

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

  @Retryable(
      retryFor = {HttpServerErrorException.class, SocketTimeoutException.class},
      maxAttempts = 7,
      backoff = @Backoff(delay = 600, multiplier = 1.5, random = true)
  )
  @PostMapping("/message-a")
  public ResponseEntity<String> messageA() {
    String messageId = IDEMPOTENCY_KEY_HOLDER.get();
    if (messageId == null) {
      messageId = UUID.randomUUID().toString();
      deliveryMetrics.incrementDeliveryMessageSent(messageId);
      IDEMPOTENCY_KEY_HOLDER.set(messageId);
      LOGGER.info("Generated new messageId: {}", messageId);
    } else {
      LOGGER.info("Retry attempt - reusing messageId: {}", messageId);
    }

    try {
      ResponseEntity<String> serviceBResponseEntity = webClient.post()
          .uri("/api/message-b")
          .bodyValue(messageId)
          .retrieve()
          .toEntity(String.class)
          .timeout(Duration.ofSeconds(1))
          .block();

      IDEMPOTENCY_KEY_HOLDER.remove();
      return ResponseEntity.ok("Message sent with ID: " + messageId);
    } catch (Exception e) {
      LOGGER.error("Error while sending a message {}", e.getClass());

      Throwable cause = e;
      while (cause != null) {
        if (cause instanceof TimeoutException) {
          LOGGER.error("Error while sending a message TimeoutException");
          throw new HttpServerErrorException(HttpStatusCode.valueOf(502), "Request timeout");
        }
        cause = cause.getCause();
      }
      throw e;
    }
  }

  @PostMapping("/error")
  public ResponseEntity<Void> error() {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  @Recover
  public ResponseEntity<String> recover(Exception e) {
    String failedMessageId = IDEMPOTENCY_KEY_HOLDER.get();
    LOGGER.error("All retry attempts failed for messageId: {}", failedMessageId, e);
    IDEMPOTENCY_KEY_HOLDER.remove();
    return ResponseEntity.status(503).body("Service B unavailable after retries");
  }
}
