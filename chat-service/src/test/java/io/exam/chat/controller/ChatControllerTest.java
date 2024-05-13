package io.exam.chat.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import io.exam.auth.JWTAuth;
import io.exam.chat.config.BeanConfig;
import io.exam.chat.controller.body.MessageBody;
import io.exam.chat.controller.response.MessageResp;
import io.exam.chat.domain.Message;
import io.exam.chat.enums.MessageType;
import io.exam.chat.repositories.MessageRepository;
import io.exam.chat.service.MessageService;
import io.exam.rest.ResponseApi;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@EnableJpaRepositories(basePackages = {"io.exam.chat.repositories"})
@EntityScan(basePackages = {"io.exam.chat.domain"})
@ComponentScan(basePackages = "io.exam.chat")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureDataJpa
@ContextConfiguration(classes = {BeanConfig.class, MessageService.class})
@WebMvcTest(ChatController.class)
class ChatControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private JWTAuth jwtAuth;

  @Autowired
  private MessageRepository messageRepository;


  @MockBean
  private SimpMessagingTemplate template;

  private Message message;
  private String token = "";
  private ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() throws ParseException, JOSEException {
    token = jwtAuth.encode(JWTClaimsSet.parse(
        Map.of("sub", 1, "username", "user1", "exp", System.currentTimeMillis() / 1000 + 3600)));
  }

  @Test
  void sendMessage() throws Exception {
    MessageBody messageBody = new MessageBody("message", MessageType.TEXT);
    var resp = this.mvc.perform(MockMvcRequestBuilders.post("/message")
            .header("Authorization", "Bearer " + token)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(messageBody)))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    var body = objectMapper.readValue(resp.getResponse().getContentAsString(),
        new TypeReference<ResponseApi<MessageResp>>() {
        });

    assertTrue(body.getData().getId() > 0);
    assertEquals(messageBody.message(), body.getData().getMessage());
    verify(template, times(1)).convertAndSend(any(String.class), any(MessageResp.class));
  }

  @Test
  void sendMessage_withoutToken_shouldReturn401() throws Exception {
    MessageBody messageBody = new MessageBody("message", MessageType.TEXT);
    this.mvc.perform(MockMvcRequestBuilders.post("/message")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(messageBody)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());

  }

  @Test
  void messageRetrieval() throws Exception {
    var from = Instant.now().minusSeconds(100).toEpochMilli();
    var size = 100;
    messageRepository.save(
        Message.builder().message("Hello").room("default").senderId(1L).senderName("user1")
            .type(MessageType.TEXT).createdAt(ZonedDateTime.now()).build());

    var resp = this.mvc.perform(MockMvcRequestBuilders.get("/message")
            .queryParam("from", String.valueOf(from)).queryParam("size", String.valueOf(size))
            .header("Authorization", "Bearer " + token)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    var body = objectMapper.readValue(resp.getResponse().getContentAsString(),
        new TypeReference<ResponseApi<List<MessageResp>>>() {
        });
    assertThat(body.getData().size()).isEqualTo(1);
  }

  // TODO: Write testcase for delete message
}