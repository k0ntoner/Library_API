package org.example.dtos;

import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CustomerDto extends UserDto {
    @PastOrPresent(message = "Date of birth cannot be in future")
    private LocalDate dateOfBirth;
}
