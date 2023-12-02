package com.project.restaurantbooking.messagetemplates;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class DeleteStaffResponse implements Serializable {

    private String requestId;
    private String operation;
    private boolean isDeleteStaffSuccessful;
    private String deleteStaffResponseMessage;

}
