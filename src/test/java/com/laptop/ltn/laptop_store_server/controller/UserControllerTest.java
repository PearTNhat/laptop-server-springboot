package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.dto.response.UserResponse;
import com.laptop.ltn.laptop_store_server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.mock.web.MockMultipartFile;
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

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    UserResponse userResponse;

    @BeforeEach
    public void setUp() {
        userResponse = UserResponse.builder()
                ._id("123")
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    @Test
    public void getUserInfo_shouldReturnUserInfo_whenAuthenticated() throws Exception {
        // Sử dụng Builder để tạo UserResponse


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

    @Test
    void testUpdateUser_withMultipartFormData() throws Exception {
        String documentJson = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@example.com\",\"phone\":\"123456789\"}";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake image content".getBytes()
        );
        Mockito.when(userService.updateUser(documentJson, file)).thenReturn(userResponse);
        // Gọi đúng endpoint, đúng kiểu multipart
        mockMvc.perform(MockMvcRequestBuilders.multipart("/user")
                                .file(file)
                                .param("document", documentJson)  // gửi dưới dạng param multipart
                        //.with(csrf()) nếu dùng security
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.firstName").value("John"));
    }
    @Test
    void testUpdateUser_missingDocument_shouldReturnBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", MediaType.IMAGE_PNG_VALUE, "fake image content".getBytes()
        );
        mockMvc.perform(MockMvcRequestBuilders.multipart("/user")
                        .file(file)) // không .param("document", ...)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    @Test
    void testUpdateUser_withoutFile_shouldStillSucceed() throws Exception {
        String documentJson = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@example.com\",\"phone\":\"123456789\"}";

        Mockito.when(userService.updateUser(Mockito.eq(documentJson), Mockito.isNull()))
                .thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/user")
                        .param("document", documentJson)) // không gửi file
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.firstName").value("John"));
    }
}
