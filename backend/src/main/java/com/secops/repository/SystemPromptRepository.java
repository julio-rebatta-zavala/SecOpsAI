package com.secops.repository;

import com.secops.entity.SystemPrompt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface SystemPromptRepository extends JpaRepository<SystemPrompt, UUID> {
	
    Optional<SystemPrompt> findByActiveTrue();

    Optional<SystemPrompt> findTopByOrderByVersionNoDesc();

}