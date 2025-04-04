package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.dto.response.UserResponse;
import com.laptop.ltn.laptop_store_server.service.UserService;
import org.springframework.security.core.context.SecurityContext;
import org.apache.hc.core5.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void getUserInfo_shouldReturnUserInfo_whenAuthenticated() throws Exception {
        // Sử dụng Builder để tạo UserResponse
        UserResponse userResponse = UserResponse.builder()
                ._id("123")
                .firstName("John")
                .lastName("Doe")
                .build();

        // Giả lập UserService trả về UserResponse
        Mockito.when(userService.getUserInfo()).thenReturn(userResponse);

        // Thực hiện yêu cầu GET và kiểm tra kết quả, với token trong header
        mockMvc.perform(MockMvcRequestBuilders.get("/user/info")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())  // Kiểm tra trạng thái HTTP là 200 OK
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data._id").value("123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastName").value("Doe"));
    }

}
