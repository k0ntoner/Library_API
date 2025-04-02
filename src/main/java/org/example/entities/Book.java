package org.example.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {

    private Long id;

    @NotBlank
    @Size(min = 1, max = 30)
    private String title;

    @Size(max = 255)
    private String description;

    @NotBlank
    @Size(max = 30)
    private String author;
}
