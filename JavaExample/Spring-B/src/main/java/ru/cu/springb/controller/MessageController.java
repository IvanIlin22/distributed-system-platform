package ru.cu.springb.controller;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MessageController {

  private final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

  @PostMapping("/message-b")
  public ResponseEntity<Void> messageB() {
    String messageId = UUID.randomUUID().toString();
    LOGGER.info("Message A: {}", messageId);

    return ResponseEntity.ok().build();
  }

  @PostMapping("/error")
  public ResponseEntity<Void> error() {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
