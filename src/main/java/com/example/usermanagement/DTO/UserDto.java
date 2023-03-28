package com.example.usermanagement.DTO;

import com.example.usermanagement.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.NamedEntityGraph;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private int userId;
    private String emailId;
    private String firstName;
    private String lastName;
    private String mobileNo;
    private List<Address> addresses;
}
