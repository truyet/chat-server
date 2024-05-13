package io.exam.chat.controller.body;

import io.exam.chat.enums.MessageType;

public record MessageBody(String message, MessageType type) {

}
