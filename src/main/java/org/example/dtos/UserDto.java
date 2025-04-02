package org.example.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.entities.BookCopy;
import org.example.entities.Order;
import org.example.entities.User;
import org.example.enums.Status;
import org.example.enums.SubscriptionType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class UserDto {
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
    private Collection<UserOrderDto> orders = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserOrderDto {
        private Long id;

        @NotBlank(message = "User cannot be blank")
        private String userId;

        @NotBlank(message = "Copy of book cannot be blank")
        private BookCopy bookCopy;

        @NotBlank(message = "Subscription type cannot be blank")
        private SubscriptionType subscriptionType;

        @NotBlank(message = "Order date type cannot be blank")
        private LocalDate orderDate;

        @NotBlank(message = "Expiration date type cannot be blank")
        private LocalDate expirationDate;

        @NotBlank(message = "Status type cannot be blank")
        private Status status;
    }
}
