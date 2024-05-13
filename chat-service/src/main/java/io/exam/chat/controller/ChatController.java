package io.exam.chat.controller;

import io.exam.chat.controller.body.MessageBody;
import io.exam.chat.controller.response.MessageResp;
import io.exam.chat.service.MessageService;
import io.exam.rest.ResponseApi;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("")
public class ChatController {

  private final MessageService messageService;

  private SimpMessagingTemplate template;

  @PostMapping("/message")
  public ResponseApi<MessageResp> sendMessage(@RequestBody MessageBody body) {
    var msg = messageService.sendDefault(body.message(), body.type());
    var msgResp = MessageResp.builder()
        .id(msg.getId())
        .senderId(msg.getSenderId())
        .senderName(msg.getSenderName())
        .room(msg.getRoom())
        .message(msg.getMessage())
        .type(msg.getType())
        .createdAt(msg.getCreatedAt().toInstant().toEpochMilli())
        .build();
    template.convertAndSend("/topic/default", msgResp);
    return ResponseApi.success(msgResp);
  }

  @GetMapping("/message")
  public ResponseApi<List<MessageResp>> messageRetrieval(@RequestParam("from") long from,
      @RequestParam(name = "size", defaultValue = "100") int size) {
    var histories = messageService.historiesDefaultRoom(
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(from),
            ZoneId.systemDefault()), size);

    var resp = histories.stream().map(msg -> {
      var builder = MessageResp.builder().id(msg.getId());
      if (msg.getDeletedAt() == null) {
        builder.senderId(msg.getSenderId())
            .senderName(msg.getSenderName())
            .room(msg.getRoom())
            .message(msg.getMessage())
            .type(msg.getType())
            .createdAt(msg.getCreatedAt().toInstant().toEpochMilli());
      } else {
        builder.deletedAt(msg.getDeletedAt().toInstant().toEpochMilli());
      }
      return builder.build();
    }).toList();
    return ResponseApi.success(resp);
  }

  @DeleteMapping("/message/{id}")
  public ResponseApi<MessageResp> sendMessage(@PathVariable("id") Long id) {
    var msg = messageService.delete(id);
    var builder = MessageResp.builder()
        .id(msg.getId())
        .deletedAt(msg.getDeletedAt().toInstant().toEpochMilli());

    var msgResp = builder.build();
    template.convertAndSend("/topic/default", msgResp);
    return ResponseApi.success(msgResp);
  }


}
