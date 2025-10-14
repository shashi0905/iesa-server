package com.technogise.iesa.expensemanagement.dto;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto {
    private UUID id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String storageKey;
    private UUID uploadedById;
    private String uploadedByName;
    private String contentType;
    private String description;
    private Instant createdAt;
}
