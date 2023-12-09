package com.project.restaurantbooking.messagetemplates;

import com.project.restaurantbooking.entity.Staff;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class AddStaffRequest implements Serializable {

    private String requestId;
    private String operation;
    private String username;
    private Staff addStaff;
    private Long restaurantId;
}
