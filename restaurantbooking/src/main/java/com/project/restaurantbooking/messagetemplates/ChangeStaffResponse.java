package com.project.restaurantbooking.messagetemplates;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ChangeStaffResponse implements Serializable {

    private String requestId;
    private String operation;
    private boolean isChangeStaffSuccessful;
    private String changeStaffResponseMessage;

}
