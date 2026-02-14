package ru.cu.springb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.cu.springb.service.DeliveryReceiver;
import ru.cu.springb.service.MessageProcessService;

@RestController
@RequestMapping("/api")
public class MessageController {

  private final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

  private final DeliveryReceiver deliveryReceiver;

  public MessageController(DeliveryReceiver deliveryReceiver) {
    this.deliveryReceiver = deliveryReceiver;
  }

  @PostMapping("/message-b")
  public ResponseEntity<String> messageB(@RequestBody String messageId) {
    LOGGER.info("Message from A: {}", messageId);

    if (MessageProcessService.processMessage(messageId) != null) {
      LOGGER.info("Message from A already process: {}", messageId);

      return ResponseEntity.ok("Message sent with ID: " + messageId);
    }

    deliveryReceiver.incrementDeliveryMessageSent(messageId);

    return ResponseEntity.ok("Message sent with ID: " + messageId);
  }

  @PostMapping("/error")
  public ResponseEntity<Void> error() {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
