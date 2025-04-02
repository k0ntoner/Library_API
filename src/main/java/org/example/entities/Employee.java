package org.example.entities;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Employee extends User {

    @NotBlank
    @Digits(integer = 10, fraction = 2, message = "Salary must have at most 10 digits in the integer part and 2 decimal places")
    private BigDecimal salary;
}
