package com.project.restaurantbooking.messagetemplates;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    private String operation;
    private String username;
    private String password;
}
