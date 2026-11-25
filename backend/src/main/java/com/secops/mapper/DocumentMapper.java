package com.secops.mapper;

import com.secops.dto.AdminDtos.DocumentView;
import com.secops.entity.RagDocument;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {
	
	public DocumentView toView(RagDocument d) {
		return new DocumentView(d.getId(), d.getOriginalName(), d.getContentSha256(), d.getStatus().name(),
				d.getSource().name(), d.getChunks(), d.getRejectionReason(), d.getCreatedAt());
	}
	
}