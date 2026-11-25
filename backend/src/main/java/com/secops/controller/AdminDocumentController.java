package com.secops.controller;

import com.secops.dto.AdminDtos.DocumentView;
import com.secops.security.SecurityContextService;
import com.secops.service.DocumentIngestionService;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController 
@RequestMapping ("/api/v1/admin/documents")
@PreAuthorize ("hasRole('ADMIN')")
public class AdminDocumentController {

    private final DocumentIngestionService svc;
    private final SecurityContextService ctx;

    public AdminDocumentController(DocumentIngestionService documentIngestionService, SecurityContextService securityContextService) {
        this.svc = documentIngestionService;
        this.ctx = securityContextService;
    }

    @GetMapping 
    Page<DocumentView> list(
            @PageableDefault (size = 50, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return svc.list(pageable);
    }

    @PostMapping (consumes = "multipart/form-data")
    DocumentView upload(@RequestPart ("file") MultipartFile file) {
        return svc.upload(ctx.current(), file);
    }

    @DeleteMapping ("/{id}")
    void delete(@PathVariable UUID id) {
        svc.delete(ctx.current(), id);
    }

}
