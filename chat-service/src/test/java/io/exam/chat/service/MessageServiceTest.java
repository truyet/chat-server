package io.exam.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.exam.auth.UserContext;
import io.exam.chat.config.BeanConfig;
import io.exam.chat.domain.Message;
import io.exam.chat.enums.MessageType;
import io.exam.chat.repositories.MessageRepository;
import io.exam.chat.service.MessageService;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableJpaRepositories(basePackages = {"io.exam.chat.repositories"})
@EntityScan(basePackages = {"io.exam.chat.domain"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureDataJpa
@ContextConfiguration(classes = {BeanConfig.class, MessageService.class})
class MessageServiceTest {

  @Autowired
  private MessageService messageService;

  @Autowired
  private MessageRepository messageRepository;


  @Test
  void sendDefault() {
    var msg = "Hello";
    var type = MessageType.TEXT;
    var userContext = UserContext.UserInfo.builder().userId(1L).username("user1").build();
    UserContext.setUser(userContext);

    var message = messageService.sendDefault(msg, type);

    assertTrue(message.getId() > 0);
    assertThat(message.getMessage()).isEqualTo(msg);
    assertThat(message.getType()).isEqualTo(type);
    assertThat(message.getSenderId()).isEqualTo(userContext.getUserId());
    assertThat(message.getSenderName()).isEqualTo(userContext.getUsername());
    assertThat(message.getRoom()).isEqualTo("default");
    assertThat(message.getCreatedAt()).isNotNull();

  }

  @Test
  void send() {
    var msg = "Hello";
    var type = MessageType.TEXT;
    var room = "room1";
    var userContext = UserContext.UserInfo.builder().userId(1L).username("user1").build();
    UserContext.setUser(userContext);

    var message = messageService.send(room, msg, type);

    assertTrue(message.getId() > 0);
    assertThat(message.getMessage()).isEqualTo(msg);
    assertThat(message.getType()).isEqualTo(type);
    assertThat(message.getSenderId()).isEqualTo(userContext.getUserId());
    assertThat(message.getSenderName()).isEqualTo(userContext.getUsername());
    assertThat(message.getRoom()).isEqualTo(room);
    assertThat(message.getCreatedAt()).isNotNull();
  }

  @Test
  void historiesDefaultRoom() {
    var fromTime = ZonedDateTime.now().minusMinutes(1);
    var size = 10;

    messageRepository.save(Message.builder().message("Hello").room("default").senderId(1L).senderName("user1").type(MessageType.TEXT).createdAt(ZonedDateTime.now()).build());

    var messages = messageService.historiesDefaultRoom(fromTime, size);

    assertThat(messages).hasSize(1);
  }

  @Test
  void historiesRoom() {
    var room = "room1";
    var fromTime = ZonedDateTime.now().minusMinutes(1);
    var size = 10;

    messageRepository.save(Message.builder().message("Hello").room(room).senderId(1L).senderName("user1").type(MessageType.TEXT).createdAt(ZonedDateTime.now()).build());

    var messages = messageService.historiesRoom(room, fromTime, size);

    assertThat(messages).hasSize(1);
  }

  // TODO: Write testcase for delete message
}