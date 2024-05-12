package io.exam.chat.controller.body;

import io.exam.chat.enums.MessageType;
import java.time.ZonedDateTime;

public record MessageBody(String message, MessageType type) {}
