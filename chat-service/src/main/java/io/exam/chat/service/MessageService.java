package io.exam.chat.service;

import io.exam.auth.UserContext;
import io.exam.chat.enums.MessageType;
import io.exam.chat.domain.Message;
import io.exam.chat.repositories.MessageRepository;
import java.time.ZonedDateTime;
import java.util.List;

import io.exam.errors.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
  private final String defaultRoom = "default";
  private final MessageRepository messageRepository;


  public Message sendDefault(String msg, MessageType type) {
    return send(defaultRoom, msg, type);
  }

  public Message send(String room, String msg, MessageType type) {
    var info = UserContext.getUser();
    var message = Message.builder().message(msg).senderId(info.getUserId()).senderName(info.getUsername()).type(type).room(room).createdAt(ZonedDateTime.now()).build();
    return messageRepository.save(message);
  }

  public Message delete(Long msgId) {
    var info = UserContext.getUser();
    var msg = messageRepository.findById(msgId);
    if (msg.isPresent()) {
      var message = msg.get();
      if (message.getSenderId() == info.getUserId()) {
        message.setDeletedAt(ZonedDateTime.now());
        return messageRepository.save(message);
      }
      throw new ServiceException("MESSAGE_NOT_OWNED");
    }
    throw new ServiceException("MESSAGE_NOT_FOUND");
  }

  public List<Message> historiesDefaultRoom(ZonedDateTime fromTime, int size) {
    return historiesRoom(defaultRoom, fromTime, size);
  }

  public List<Message> historiesRoom(String room, ZonedDateTime fromTime, int size) {
    if (size <= 0 || size > 100) {
      size = 100;
    }
    return messageRepository.findAllByRoomFromTime(room, fromTime, PageRequest.ofSize(size).withSort(
        Sort.by(Order.desc("id"))));
  }

}
