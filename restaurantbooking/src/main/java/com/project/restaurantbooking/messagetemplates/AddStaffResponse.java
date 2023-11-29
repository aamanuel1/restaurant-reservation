package com.project.restaurantbooking.messagetemplates;

import com.project.restaurantbooking.entity.Staff;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class AddStaffResponse implements Serializable {

    private String requestId;
    private String operation;
    private boolean isAddStaffSuccessful;
    private String addStaffResponseMessage;
}
