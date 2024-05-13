package io.exam.ws.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

  private SimpMessagingTemplate template;

  @MessageMapping("/default")
  @SendTo("/topic/default")
  public MessageResp sendMessage(MessageBody msg) throws Exception {
    return MessageResp.builder().message(msg.message()).type(msg.type()).build();
  }

}