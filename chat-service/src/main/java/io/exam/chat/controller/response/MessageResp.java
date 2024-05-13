package io.exam.chat.controller.response;

import io.exam.chat.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResp {

  private long id;
  private long senderId;
  private String senderName;
  private String room;
  private String message;
  private MessageType type;
  private long createdAt;
  private long deletedAt;
}