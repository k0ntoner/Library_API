package org.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.dtos.UserDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class User {

    private String id;

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 30, message = "First name must be at most 30 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 30, message = "Last name must be at most 30 characters")
    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Size(max = 30, message = "Email must be at most 30 characters")
    private String email;

    @NotBlank(message = "First name cannot be blank")
    @Pattern(regexp = "^\\+\\d{13}$", message = "Phone number must start with '+' and contain only up to 13 digits")
    private String phoneNumber;

    @NotBlank(message = "Orders cannot be blank")
    @Builder.Default
    private Collection<Order> orders = new ArrayList<>();

}