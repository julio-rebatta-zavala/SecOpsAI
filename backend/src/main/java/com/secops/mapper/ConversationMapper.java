package com.secops.mapper;

import com.secops.dto.ConversationDtos.ConversationSummary;
import com.secops.entity.Conversation;
import org.springframework.stereotype.Component;

@Component
public class ConversationMapper {
	
	public ConversationSummary toSummary(Conversation c) {
		return new ConversationSummary(c.getId(), c.getTitle(), c.getCreatedAt(), c.getUpdatedAt());
	}
	
}