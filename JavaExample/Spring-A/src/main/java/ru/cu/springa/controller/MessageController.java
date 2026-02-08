package ru.cu.springa.controller;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class MessageController {

  private final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);
  private final RestTemplate restTemplate;
  private final String serviceBUrl;

  public MessageController(
      RestTemplate restTemplate,
      @Value("${service-b.url}") String serviceBUrl
  ) {
    this.restTemplate = restTemplate;
    this.serviceBUrl = serviceBUrl;
  }

  @PostMapping("/message-a")
  public ResponseEntity<String> messageA() {
    String messageId = UUID.randomUUID().toString();
    LOGGER.info("Message A: {}", messageId);

    String url = serviceBUrl + "/api/message-b";
    restTemplate.postForEntity(url, messageId, Void.class);

    return ResponseEntity.ok("Message sent with ID: " + messageId);
  }

  @PostMapping("/error")
  public ResponseEntity<Void> error() {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
