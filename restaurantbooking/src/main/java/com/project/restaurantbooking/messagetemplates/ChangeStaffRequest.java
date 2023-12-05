package com.project.restaurantbooking.messagetemplates;

import com.project.restaurantbooking.entity.Staff;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ChangeStaffRequest implements Serializable {

    private String requestId;
    private String operation;
    private String username;
    private Long changeStaffId;
    private Staff changeStaff;


}
