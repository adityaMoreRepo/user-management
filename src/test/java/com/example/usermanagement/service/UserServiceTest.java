package com.example.usermanagement.service;

import com.example.usermanagement.DTO.UserDto;
import com.example.usermanagement.entity.Address;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


/*
The @ExtendWith annotation is used to load a JUnit 5 extension.
JUnit defines an extension API, which allows a third-party vendor
like Mockito to hook into the lifecycle of running test classes
and add additional functionality.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository repo;
    @Mock
    private BCryptPasswordEncoder encoder;
    @InjectMocks
    private UserService service;
    private User user;

    @BeforeEach
    public void setup() {
        //mock User repository
//        repo = Mockito.mock(UserRepository.class);
        //mock encoder
//        encoder = Mockito.mock(BCryptPasswordEncoder.class);
        //Inject repository
        service = new UserService(repo, encoder);
        //Create a User
        Address address = Address.builder()
                .addressType("Permanent")
                .houseNo(11)
                .street("MG")
                .city("Nashik")
                .country("India")
                .build();
        List<Address> addresses = new ArrayList<>(Arrays.asList(address));
        user = User.builder()
                .userId(1)
                .userName("adi")
                .password("adi@123")
                .active(true)
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .mobileNo("9035394930")
                .addresses(addresses)
                .build();

    }

    // Unit test for CreateUser Method

    @Test
    @DisplayName("Unit test for createUser method")
    void givenUser_whenCreateUser_thenReturnUserDTO() {
        //given
        //Create fetchedUser
        User fetchedUser = User.builder()
                .userId(1)
                .userName("adi")
                .password("adi@123")
                .active(true)
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .mobileNo("9035394930")
                .build();
        given(repo.save(user))
                .willReturn(fetchedUser);
        given(encoder.encode(user.getPassword()))
                .willReturn("$2a$12$154uuo59SP8YJ2MeXr9zBOI9JGkVThbxdLebZ6gmoAOhZRr5IRz7y");
        //when
        UserDto savedUser = service.createUser(user);
        //then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUserId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Give Access to User")
    void givenUserRole_whenGivenAccess_thenReturnConfirmation() {
        //given
        //Optional User
        //Create fetchedUser
        User fetchedUser = User.builder()
                .userId(1)
                .userName("adi")
                .password("adi@123")
                .roles("ROLE_USER")
                .active(true)
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .mobileNo("9035394930")
                .build();

        // Logged-in user or Principal
        User loggedInUser = User.builder()
                .userId(2)
                .userName("adi")
                .password("adi@123")
                .roles("ROLE_ADMIN")
                .active(true)
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .mobileNo("9035394930")
                .build();
        //Create a dummy Principal
        Principal principal = (UserPrincipal) () -> "adi";
        // Get Logged in User
        given(repo.findByUserName(principal.getName()))
                .willReturn(Optional.ofNullable(loggedInUser));
        //Get User by ID whose Role is supposed to be changed.
        given(repo.findById(fetchedUser.getUserId()))
                .willReturn(Optional.ofNullable(fetchedUser));
        //when
        String userRole = "ROLE_ADMIN";
        String received = service.giveAccessToUser(fetchedUser.getUserId(), userRole, principal);
        //then
        String expected = "Hi, " + fetchedUser.getUserName() + ". New Role assign to you by " + principal.getName();
        assertThat(expected).isEqualTo(received);
    }

    @Test
    @DisplayName("Access Not given to the User")
    void givenUserRole_whenGivenAccess_thenReturnFailure() {
        //given
        //Optional User
        //Create fetchedUser
        User fetchedUser = User.builder()
                .userId(1)
                .userName("adi")
                .password("adi@123")
                .roles("ROLE_USER")
                .active(true)
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .mobileNo("9035394930")
                .build();

        // Logged-in user or Principal
        User loggedInUser = User.builder()
                .userId(2)
                .userName("adi")
                .password("adi@123")
                .roles("ROLE_USER")
                .active(true)
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .mobileNo("9035394930")
                .build();
        //Create a dummy Principal
        Principal principal = (UserPrincipal) () -> "adi";
        // Get Logged in User
        given(repo.findByUserName(principal.getName()))
                .willReturn(Optional.ofNullable(loggedInUser));
        //Get User by ID whose Role is supposed to be changed.
        given(repo.findById(fetchedUser.getUserId()))
                .willReturn(Optional.ofNullable(fetchedUser));
        //when
        String userRole = "ROLE_ADMIN";
        String received = service.giveAccessToUser(fetchedUser.getUserId(), userRole, principal);
        //then
        String expected = "Hi, " + principal.getName() + " you don't have permission to assign " + userRole;
        assertThat(expected).isEqualTo(received);
    }

    @Test
    @DisplayName("Unit test for getting user by User ID")
    void givenUserId_whenGetUserById_thenReturnUserDto() {
        //given
        //Create fetchedUser
        User fetchedUser = User.builder()
                .userId(1)
                .userName("adi")
                .password("adi@123")
                .active(true)
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .mobileNo("9035394930")
                .build();
        //OptionalUser
        Optional<User> optionalUser = Optional.ofNullable(fetchedUser);
        given(repo.findById(1))
                .willReturn(optionalUser);
        //when
        UserDto userDto = service.getUserById(1);
        //then
        assertThat(userDto).isNotNull();
        assertThat(userDto.getUserId()).isEqualTo(1);
    }

    @Test()
    @DisplayName("Unit test for not getting user by User ID")
    void givenUserId_whenGetUserById_thenThrowsException() {
        //given
        //OptionalUser
        given(repo.findById(2))
                .willReturn(Optional.empty());
        //when
        assertThrows(UserNotFoundException.class, () -> service.getUserById(2));
        //then

    }

    @Test
    @DisplayName("Get All Users By Page")
    void givenPageRequest_whenFindAll_thenReturnAllUsersPage() {
        //given
        //PageRequest
        PageRequest pageRequest = PageRequest.of(0, 1);
        //Page of Users
        User user1 = User.builder()
                .userId(1)
                .userName("user1")
                .password("adi@123")
                .active(true)
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .mobileNo("9035394928")
                .build();
        User user2 = User.builder()
                .userId(2)
                .userName("user2")
                .password("adi@123")
                .active(true)
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .mobileNo("9035394930")
                .build();
        List<User> userList = new ArrayList<>(Arrays.asList(user1, user2));
        Page<User> users = new PageImpl<>(userList, pageRequest, userList.size());
        given(repo.findAll(pageRequest))
                .willReturn(users);
        //when
        Page<UserDto> allUsersByPage = service.getAllUsersByPage(pageRequest);
        //then
        assertThat(allUsersByPage).isNotNull();
        assertThat(allUsersByPage.stream().count()).isEqualTo(2);
    }

    @Test
    @DisplayName("Remove User By User ID")
    void givenUserID_whenFindByIDDeleteBYID_thenReturnMessage() {
        //given User ID
        given(repo.findById(1))
                .willReturn(Optional.of(user));
        //capture User Argument to verify, as it is a void method
        ArgumentCaptor<User> valueCapture = ArgumentCaptor.forClass(User.class);
        doNothing().when(repo).delete(valueCapture.capture());

        //when
        ResponseEntity<String> stringResponseEntity = service.removeUser(1);

        //then
        String expected = "The user with userID " + user.getUserId() + " has been deleted successfully";
        //Verify that how many times repo.delete() was accessed
        verify(repo, times(1)).delete(user);
        //Check for captured value
        assertEquals(user, valueCapture.getValue());
        //check return message
        assertEquals(expected, stringResponseEntity.getBody());

    }

    @Test
    void givenNewUser_whenUpdateUser_thenReturnNewUser() {
        //given
        Address address = Address.builder()
                .houseNo(11)
                .street("MG")
                .city("Nashik")
                .country("India")
                .build();
        List<Address> addresses = new ArrayList<>(Arrays.asList(address));
        User newUser = User.builder()
                .userId(1)
                .userName("adi")
                .password("adi@123")
                .active(true)
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .addresses(addresses)
                .mobileNo("8070567510")
                .build();
        given(repo.findById(1))
                .willReturn(Optional.of(user));
        //when Mobile number is updated
        UserDto userDto = service.updateUser(1, newUser);
        //then check Mobile No
        assertThat(userDto).isNotNull();
        verify(repo).save(user);
        assertThat(userDto.getMobileNo()).isEqualTo(newUser.getMobileNo());
    }

    @Test
    @DisplayName("Add Address to the existing User")
    void givenAddress_whenAddAddress_thenReturnUpdatedUser() {
        //given
        given(repo.findById(1))
                .willReturn(Optional.of(user));
        //New address
        Address address = Address.builder()
                .addressType("Temporary")
                .houseNo(12)
                .street("Main Road")
                .city("Pune")
                .country("India")
                .build();
        //when
        UserDto userDto = service.addAddressToUser(1, address);
        //then
        assertThat(userDto).isNotNull();
        assertThat(userDto.getAddresses().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Update Password credentials for logged in User")
    void givenPrincipal_whenUpdateCredentials_thenReturnResult() {
        //given
        // Logged-in user or Principal
        User loggedInUser = User.builder()
                .userId(2)
                .userName("adi")
                .password("$2a$12$gajkO9rXT8quBfxeMasCTu0jRx//3.Ke.95bbUkwzz.D.EM7XyBDa")//pwd: adi@123
                .roles("ROLE_ADMIN")
                .active(true)
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .mobileNo("9035394930")
                .build();
        //Create a dummy Principal
        Principal principal = (UserPrincipal) () -> "adi";
        // Get Logged in User
        given(repo.findByUserName(principal.getName()))
                .willReturn(Optional.of(loggedInUser));
        //Encoder match method
        given(encoder.matches("adi@123", loggedInUser.getPassword()))
                .willReturn(true);
        String expectedMessage = "You have successfully changed the password";
        //when
        String message = service.updateCredentials("newPassword", "adi@123", principal);
        //then
        assertEquals(message, expectedMessage);
    }

    @Test
    @DisplayName("Find the List of Users")
    void findAllTest() {
        //given
        User user1 = User.builder()
                .userId(1)
                .userName("user1")
                .password("adi@123")
                .active(true)
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .mobileNo("9035394928")
                .build();
        User user2 = User.builder()
                .userId(2)
                .userName("user2")
                .password("adi@123")
                .active(true)
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .mobileNo("9035394930")
                .build();
        List<User> userList = new ArrayList<>(Arrays.asList(user1, user2));
        given(repo.findAll())
                .willReturn(userList);
        //when
        List<UserDto> userDtoList = service.findAll();
        //then
        assertThat(userDtoList).isNotNull();
        assertThat(userDtoList.size()).isEqualTo(2);

    }
}