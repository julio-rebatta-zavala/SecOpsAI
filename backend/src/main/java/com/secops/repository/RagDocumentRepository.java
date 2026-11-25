package com.secops.repository;

import com.secops.entity.RagDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface RagDocumentRepository extends JpaRepository<RagDocument, UUID> {
	
    Optional<RagDocument> findByContentSha256(String hash);

}