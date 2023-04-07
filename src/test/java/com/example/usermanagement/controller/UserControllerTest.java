package com.example.usermanagement.controller;

import com.example.usermanagement.DTO.UserDto;
import com.example.usermanagement.entity.Address;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.service.GroupUserDetails;
import com.example.usermanagement.service.GroupUserDetailsService;
import com.example.usermanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

//WebMVC annotation helps in testing Spring MVC related dependencies
//Essentially We don't have to load an Entire application, it only loads
//required beans in Controller layer
@WebMvcTest
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc; // To call Rest APIs
    //Create a UserService Bean in the Context.
    //Here we are actually going to take help from Spring framework for testing
    //Hence no isolation like @Mock which is mockito framework.
    @MockBean
    private UserService service;
    @MockBean
    private GroupUserDetailsService groupUserDetailsService;
    @Autowired
    private ObjectMapper objectMapper;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
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
                .roles("ROLE_USER")
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .mobileNo("9035394930")
                .addresses(addresses)
                .build();

        //User DTO
        userDto = UserDto.builder()
                .userId(1)
                .userName("adi")
                .active(true)
                .roles("ROLE_USER")
                .emailId("adi@gmail.com")
                .firstName("Adi")
                .lastName("More")
                .mobileNo("9035394930")
                .addresses(addresses)
                .build();
    }

    @Test
    @DisplayName("Given user is saved by calling this endpoint")
    void givenUser_whenAddUser_thenReturnSavedUser() throws Exception {
        //given
        //Group User Details
        GroupUserDetails groupUserDetails = new GroupUserDetails(user);
        given(service.createUser(ArgumentMatchers.any(User.class)))
                .willReturn(userDto); //For stubbing methods we use ArgumentMatchers and ArgumentCaptors
        given(groupUserDetailsService.loadUserByUsername("adi"))
                .willReturn(groupUserDetails);
        //when
        ResultActions response = mockMvc.perform(post("/v1/user/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));
        //then
        response.andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName", CoreMatchers.is(user.getUserName())));
    }

    //Positive Scenario
    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})//Logged in user/Principal
    void givenUserIDAndRole_whenGivenAccess_thenReturnResult() throws Exception {
        //given
        ArgumentCaptor<Principal> principalArgumentCaptor = ArgumentCaptor.forClass(Principal.class);
        String message = "Hi, " + user.getUserName() + ". New Role assign to you by admin";
        given(service.giveAccessToUser(eq(user.getUserId()), eq("ROLE_ADMIN"), principalArgumentCaptor.capture()))
                .willReturn(message);
        //when
        //Create a request
        RequestBuilder requestBuilder = get("/v1/access/1/ROLE_ADMIN")
//                .principal(mockPrincipal)
                .accept(MediaType.APPLICATION_JSON);
        //Perform Request
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        //then
        assertEquals(200, status);
        verify(service, times(1)).giveAccessToUser(1, "ROLE_ADMIN", principalArgumentCaptor.getValue());
    }

    //Negative Scenario
    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_USER"})
    void givenUserWithRoleUser_whenGiveAccess_thenIsForbidden() throws Exception {
        //given
        //Create a request
        RequestBuilder requestBuilder = get("/v1/access/1/ROLE_ADMIN")
                .accept(MediaType.APPLICATION_JSON);

        //when
        //Perform Request
        ResultActions resultActions = mockMvc.perform(requestBuilder)
                .andDo(print());

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isForbidden());

    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void givenUserId_whenFindByUserId_thenReturnUser() throws Exception {
        //given
        given(service.getUserById(user.getUserId()))
                .willReturn(userDto);
        //when
        RequestBuilder requestBuilder = get("/v1/byId/1")
                .accept(MediaType.APPLICATION_JSON);
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        //then
        resultActions
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.emailId",
                        CoreMatchers.is("adi@gmail.com")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void getAllUsersByPage() throws Exception {
        //given
        //page request
        //No need to create pageRequest if you hydrate your Controller's pageable Object correctly by
        //putting queries in the URL and using ArgumentMatchers.any(PageRequest.class) for
        //method arguments while stubbing
        PageRequest pageRequest = PageRequest.of(0, 1);
        List<UserDto> userDtos = new ArrayList<>(Arrays.asList(userDto));
        Page<UserDto> userDtoPage = new PageImpl<>(userDtos, pageRequest, userDtos.size());
        given(service.getAllUsersByPage(any(Pageable.class)))
                .willReturn(userDtoPage);
        //when
        RequestBuilder requestBuilder = get("/v1/AllUsersByPage?page=0&size=1")
                .accept(MediaType.APPLICATION_JSON);
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        //then
        resultActions
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.number", CoreMatchers.is(0)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void removeUser() throws Exception {
        //given
        ResponseEntity<String> response = ResponseEntity.ok().body("The user with userID " + user.getUserId() + " has been deleted successfully");
        given(service.removeUser(user.getUserId()))
                .willReturn(response);

        //when
        RequestBuilder requestBuilder = delete("/v1/removeUser/1")
                .accept(MediaType.APPLICATION_JSON);
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        //then
        resultActions
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updateUser() {
    }

    @Test
    void addAddressToUse() {
    }

    @Test
    void updateCredentials() {
    }

    @Test
    void exportCSV() {
    }
}