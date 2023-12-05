package com.project.restaurantbooking.messagetemplates;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class DeleteStaffRequest implements Serializable {

    private String requestId;
    private String operation;
    private String username;
    private String deleteByStaffUsername;
    private Long deleteByStaffId;
}
