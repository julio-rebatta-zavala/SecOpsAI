package com.secops.repository;

import com.secops.entity.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {
}