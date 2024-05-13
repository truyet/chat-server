package io.exam.chat.repositories;

import io.exam.chat.domain.Message;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MessageRepository extends CrudRepository<Message, Long>,
    PagingAndSortingRepository<Message, Long> {

  @Query("FROM Message m WHERE m.room = :room AND m.createdAt > :from")
  List<Message> findAllByRoomFromTime(String room, ZonedDateTime from, Pageable pageable);

}
