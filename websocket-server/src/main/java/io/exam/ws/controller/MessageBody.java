package io.exam.ws.controller;


import io.exam.ws.enums.MessageType;

public record MessageBody(String message, MessageType type) {}
