package com.technogise.iesa.expensemanagement.domain;

import com.technogise.iesa.shared.domain.BaseEntity;
import com.technogise.iesa.usermanagement.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Document entity representing an attached file (receipt/invoice)
 */
@Entity
@Table(name = "expense_documents", indexes = {
    @Index(name = "idx_document_expense", columnList = "expense_id"),
    @Index(name = "idx_document_uploader", columnList = "uploaded_by_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Document extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @Column(name = "file_name", nullable = false, length = 500)
    private String fileName;

    @Column(name = "file_type", nullable = false, length = 100)
    private String fileType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "storage_key", nullable = false, length = 500)
    private String storageKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploaded_by_id", nullable = false)
    private User uploadedBy;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "description", length = 500)
    private String description;

    /**
     * Get file extension from file name
     */
    @Transient
    public String getFileExtension() {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }

    /**
     * Check if file is an image
     */
    @Transient
    public boolean isImage() {
        String ext = getFileExtension();
        return "jpg".equals(ext) || "jpeg".equals(ext) || "png".equals(ext) || "gif".equals(ext);
    }

    /**
     * Check if file is a PDF
     */
    @Transient
    public boolean isPdf() {
        return "pdf".equals(getFileExtension());
    }
}
