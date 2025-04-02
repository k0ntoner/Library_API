package org.example.entities;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.Status;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookCopy {
    private Long id;

    @NotBlank(message = "Book cannot be blank")
    private Book book;

    @NotBlank(message = "Status cannot be blank")
    private Status status;
}
