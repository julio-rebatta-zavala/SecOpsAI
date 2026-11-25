package com.secops.repository;

import com.secops.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;
import java.util.*;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
	
    Page<Conversation> findByOwnerId(UUID ownerId, Pageable pageable);

    Optional<Conversation> findByIdAndOwnerId(UUID id, UUID ownerId);

}