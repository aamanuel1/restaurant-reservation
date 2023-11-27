package com.project.restaurantbooking.messagetemplates;

import com.project.restaurantbooking.entity.Staff;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {
    private String requestId;
    private String operation;
    private boolean successfulLogin;
    private Staff staff;

}
