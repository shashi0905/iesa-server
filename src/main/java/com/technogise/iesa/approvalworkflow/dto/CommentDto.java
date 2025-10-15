package com.technogise.iesa.approvalworkflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private UUID id;
    private UUID expenseId;
    private UUID authorId;
    private String authorName;
    private String content;
    private Boolean isInternal;
    private Instant createdAt;
}
