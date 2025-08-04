package com.management.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("books")
public class Book {

    @Id
    private Long id;

    @NotBlank(message = "ISBN is required")
    private String isbn;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    private String category;

    @Min(value = 1000, message = "Publication year must be valid")
    private Integer publicationYear;

    @NotNull
    @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies = 1;

    @NotNull
    @Min(value = 0, message = "Available copies cannot be negative")
    private Integer availableCopies = 1;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
