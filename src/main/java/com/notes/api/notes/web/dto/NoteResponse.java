package com.notes.api.notes.web.dto;

import java.time.LocalDateTime;

public record NoteResponse(
        Long id,
        String title,
        String content,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
