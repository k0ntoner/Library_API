package org.example.entities;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.Status;
import org.example.enums.SubscriptionType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;

    @NotBlank(message = "User cannot be blank")
    private User user;

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
