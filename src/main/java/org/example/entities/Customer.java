package org.example.entities;

import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Customer extends User {

    @PastOrPresent(message = "Date of birth cannot be in future")
    private LocalDate dateOfBirth;
}
