package com.secops.repository;

import com.secops.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface MessageRepository extends JpaRepository<Message, UUID> {
	
    List<Message> findByConversationIdOrderByCreatedAtAsc(UUID conversationId);

}