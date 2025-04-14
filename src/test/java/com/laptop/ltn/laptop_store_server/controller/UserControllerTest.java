package com.laptop.ltn.laptop_store_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laptop.ltn.laptop_store_server.dto.request.UpdateBlockRequest;
import com.laptop.ltn.laptop_store_server.dto.request.UpdateRoleRequest;
import com.laptop.ltn.laptop_store_server.dto.request.WishListRequest;
import com.laptop.ltn.laptop_store_server.dto.response.UserResponse;
import com.laptop.ltn.laptop_store_server.entity.User;
import com.laptop.ltn.laptop_store_server.service.UserService;
import com.laptop.ltn.laptop_store_server.service.UserServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    UserResponse userResponse;
    User user;
    @BeforeEach
    void setUp() {
        user  = User.builder()
                ._id("123")
                .firstName("John")
                .build();

        userResponse = UserResponse.builder()
                ._id("123")
                .firstName("John")
                .email("john@doe.com")
                .build();
    }
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
        when(userService.getUserInfo()).thenReturn(userResponse);

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
    void testGetAllUsers() throws Exception {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("email", "example@example.com");
        User user = User.builder()._id("1").email("example@example.com").build();
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userService.findAllWithFilters(anyMap(), any(Pageable.class))).thenReturn(userPage);

        mockMvc.perform(get("/user/get-users")
                        .param("email", "example@example.com")
                        .param("page", "1")
                        .param("limit", "10")
                        .param("sort", "email,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.counts").value(1))
                .andExpect(jsonPath("$.data[0].email").value("example@example.com"));
    }
    @Test
    void testUpdateUser_withAvatar() throws Exception {
        UserResponse userResponse = UserResponse.builder()
                ._id("1")
                .firstName("Updated Name")
                .build();

        String documentJson = objectMapper.writeValueAsString(
                UserResponse.builder()._id("1").firstName("Updated Name").build()
        );

        MockMultipartFile avatar = new MockMultipartFile(
                "avatar", "avatar.jpg", "image/jpeg", "fake-image".getBytes()
        );

        when(userService.updateUser(eq(documentJson), any(MultipartFile.class)))
                .thenReturn(userResponse);

        mockMvc.perform(multipart(HttpMethod.PUT, "/user")
                        .file(avatar)
                        .param("document", documentJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data._id").value("1"))
                .andExpect(jsonPath("$.data.firstName").value("Updated Name"));
    }
    @Test
    void testUpdateUser_withoutAvatar_usingBuilder() throws Exception {
        UserResponse userResponse = UserResponse.builder()
                ._id("1")
                .firstName("No Avatar Update")
                .build();

        String documentJson = objectMapper.writeValueAsString(
                UserResponse.builder()._id("1").firstName("No Avatar Update").build()
        );

        when(userService.updateUser(eq(documentJson), isNull()))
                .thenReturn(userResponse);

        mockMvc.perform(multipart(HttpMethod.PUT, "/user")
                        .param("document", documentJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("No Avatar Update"));
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

        mockMvc.perform(put("/user/admin")
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

        mockMvc.perform(put("/user/admin/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Block user successfully"));

        verify(userService).updateBlock(any(UpdateBlockRequest.class));
    }

}
