package com.laptop.ltn.laptop_store_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laptop.ltn.laptop_store_server.dto.request.UpdateBlockRequest;
import com.laptop.ltn.laptop_store_server.dto.request.UpdateRoleRequest;
import com.laptop.ltn.laptop_store_server.dto.request.WishListRequest;
import com.laptop.ltn.laptop_store_server.dto.response.UserResponse;
import com.laptop.ltn.laptop_store_server.service.UserService;
import com.laptop.ltn.laptop_store_server.service.UserServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("TCC-001: Get user info should return user info when authenticated")
    public void TCC001_getUserInfo_shouldReturnUserInfo_whenAuthenticated() throws Exception {
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
                .andExpect(status().isOk()) // Kiểm tra trạng thái HTTP là 200 OK
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data._id").value("123"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"));
    }
    @Test
    void testUpdateWishlist() throws Exception {
        WishListRequest request = WishListRequest.builder()
                .product("product123")
                .build();

        mockMvc.perform(put("/user/wish-list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update wishlist successfully"));

        verify(userService).updateWishlist(any(WishListRequest.class));
    }

    @Test
    void testUpdateUserByAdmin() throws Exception {
        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .userId("user123")
                .role("admin")
                .build();

        mockMvc.perform(put("/user//admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update user successfully"));

        verify(userService).updateRole(any(UpdateRoleRequest.class));
    }

    @Test
    void testBlockUser() throws Exception {
        UpdateBlockRequest request = UpdateBlockRequest.builder()
                .userId("user123")
                .isBlocked(true)
                .build();

        mockMvc.perform(put("/user//admin/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Block user successfully"));

        verify(userService).updateBlock(any(UpdateBlockRequest.class));
    }

}
