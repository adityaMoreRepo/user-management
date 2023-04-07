package com.example.usermanagement.DTO;

import com.example.usermanagement.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private int userId;

    private String userName;
    private Boolean active;
    private String roles;
    private String emailId;
    private String firstName;
    private String lastName;
    private String mobileNo;
    private List<Address> addresses;
}
