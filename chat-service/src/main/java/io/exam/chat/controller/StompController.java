package io.exam.chat.controller;

import io.exam.auth.UserContext;
import io.exam.auth.UserContext.UserInfo;
import io.exam.chat.controller.body.MessageBody;
import io.exam.chat.controller.response.MessageResp;
import io.exam.chat.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class StompController {

	private final MessageService messageService;

  public StompController(MessageService messageService) {
    this.messageService = messageService;
  }

  @MessageMapping("/default")
	@SendTo("/topic/default")
	public MessageResp sendMessage(SimpMessageHeaderAccessor headerAccessor, MessageBody body) throws Exception {
		UserContext.setUser((UserInfo) headerAccessor.getUser());
		var msg = messageService.sendDefault(body.message(), body.type());
		return MessageResp.builder()
				.id(msg.getId())
				.senderId(msg.getSenderId())
				.senderName(msg.getSenderName())
				.room(msg.getRoom())
				.message(msg.getMessage())
				.type(msg.getType())
				.createdAt(msg.getCreatedAt().toInstant().toEpochMilli())
				.build();
	}

}