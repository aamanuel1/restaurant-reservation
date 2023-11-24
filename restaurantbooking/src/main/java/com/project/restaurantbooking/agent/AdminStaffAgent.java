package com.project.restaurantbooking.agent;

import com.project.restaurantbooking.entity.Restaurant;
import com.project.restaurantbooking.entity.RestaurantTable;
import com.project.restaurantbooking.entity.Shift;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.repo.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class AdminStaffAgent extends StaffAgent{

    public AdminStaffAgent(){
        super();
    }

    public AdminStaffAgent(StaffRepository staffRepository){
        super(staffRepository);
    }

    //Staff repository CRUD ops.
    //TODO: implement as behaviours in setup.
    public void addStaff(Staff newStaff){
        Optional<Staff> staffToAdd = searchStaffByUsername(newStaff.getUsername());
        if(staffToAdd.isPresent()){
            throw new IllegalStateException("Username already exists");
        }
    }

    public void deleteStaffById(Long id){
        this.staffRepository.deleteById(id);
    }

    public void deleteStaffByUsername(String username){
        Optional<Staff> deleteUsername = searchStaffByUsername(username);
        if(deleteUsername.isEmpty()){
            throw new IllegalStateException("Username not found.");
        }
    }

    public void changeStaffAttributes(Long staffID, Staff staffChange){
        Optional<Staff> changeThisStaff = staffRepository.findUserByStaffId(staffID);
        if(changeThisStaff.isPresent()){
            Staff staff = changeThisStaff.get();
            //Go through each attribute one by one and check for null before changing, then save to repo
            if(staffChange.getFirstName().isBlank()){
                staff.setFirstName(staffChange.getFirstName());
            }
            if(staffChange.getLastName().isBlank()){
                staff.setLastName(staffChange.getLastName());
            }
            if(staffChange.getUsername().isBlank()){
                staff.setUsername(staffChange.getUsername());
            }
            if(staffChange.isAdmin() != staff.isAdmin()){
                staff.setAdmin(staffChange.isAdmin());
            }
            if(staffChange.getPassword().isBlank()){
                staff.setPassword(staffChange.getPassword());
            }

            staffRepository.save(staff);
        }
    }

    public Optional<Staff> searchStaffById(Long staffId){
        return staffRepository.findUserByStaffId(staffId);
    }

    public void createEmptyTable(Long restaurantId, int tableOccupancyNum, boolean available){
        RestaurantTable createdEmptyTable = new RestaurantTable(tableOccupancyNum, available, null);
        //TODO: implement in scheduling agent, replace restaurantId
    }

    public void createTable(Long restaurantId, int tableOccupancyNum, boolean available, ArrayList<Shift> timeslots){
        RestaurantTable createdTable = new RestaurantTable(null, tableOccupancyNum, available, null);
        //TODO: implement in scheduling agent
    }

    public Optional<Staff> searchStaffByUsername(String username){
        return staffRepository.findByUsername(username);
    }

}
