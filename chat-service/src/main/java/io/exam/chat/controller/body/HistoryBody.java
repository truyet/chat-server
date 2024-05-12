package io.exam.chat.controller.body;

import java.time.ZonedDateTime;

public record HistoryBody(ZonedDateTime from, int size) {}
