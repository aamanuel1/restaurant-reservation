package com.project.restaurantbooking.messagetemplates;

import com.project.restaurantbooking.entity.Staff;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class SearchStaffRequest implements Serializable {

    private String requestId;
    private String operation;
    private String username;
    private String searchUsername;
    private Long searchUserId;
}
