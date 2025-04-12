package com.laptop.ltn.laptop_store_server.service;

import com.cloudinary.Cloudinary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laptop.ltn.laptop_store_server.dto.request.UserUpdateRequest;
import com.laptop.ltn.laptop_store_server.dto.response.UserResponse;
import com.laptop.ltn.laptop_store_server.entity.Image;
import com.laptop.ltn.laptop_store_server.entity.User;
import com.laptop.ltn.laptop_store_server.exception.ErrorCode;
import com.laptop.ltn.laptop_store_server.mapper.UserMapper;
import com.laptop.ltn.laptop_store_server.repository.UserRepository;
import com.laptop.ltn.laptop_store_server.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    MongoTemplate mongoTemplate;
    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;
    @Mock
    UploadImageFile uploadImageFile;
    @Mock
    Cloudinary cloudinary;

    User user;
    UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                ._id("123")
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        userResponse = UserResponse.builder()
                ._id("123")
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("123");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }
    @Test
    void getUserInfo_ShouldReturnUserResponse_WhenUserExists() {
        when(userRepository.findById("123")).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUserInfo();

        assertEquals("john@example.com", result.getEmail());
        verify(userRepository).findById("123");
        verify(userMapper).toUserResponse(user);
    }
    @Test
    void getUserInfo_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById("123")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserInfo());

        assertEquals(ErrorCode.USER_NOT_EXISTED.getMessage(), exception.getMessage());
    }
    @Test
    void findAllWithFilters_ShouldReturnFilteredPage() {
        Map<String, String> queryParams = Map.of("firstName", "John");
        Pageable pageable = PageRequest.of(0, 10);
        User user = User.builder().firstName("John").build();

        when(mongoTemplate.count(any(Query.class), eq(User.class))).thenReturn(1L);
        when(mongoTemplate.find(any(Query.class), eq(User.class))).thenReturn(List.of(user));

        Page<User> result = userService.findAllWithFilters(queryParams, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).getFirstName());
    }

    @Test
    void updateUser_ShouldUpdateFields_WhenValidRequestWithoutImage() throws Exception {
        UserUpdateRequest request = UserUpdateRequest.builder()
                .firstName("Johnny")
                .lastName("Bravo")
                .email("johnny@example.com")
                .phone("123456789")
                .build();

        String json = new ObjectMapper().writeValueAsString(request);
        MockMultipartFile file = new MockMultipartFile("file", new byte[0]);

        when(userRepository.findById("123")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.updateUser(json, file);

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository).save(any());
    }
//    @Test
//    void updateUser_ShouldUploadImageAndDeleteOld_WhenFilePresent() throws Exception {
//        String userId = "user123";
//        String json = "{\"firstName\":\"Updated\"}";
//        MultipartFile file = mock(MultipartFile.class);
//        when(file.isEmpty()).thenReturn(false);
//
//        Map<String, Object> uploadRes = Map.of("url", "image.jpg", "public_id", "img123");
//        User user = User.builder()._id(userId).avatar(Image.builder().public_id("oldImg").build()).build();
//        UserResponse response = UserResponse.builder()._id(userId).firstName("Updated").build();
//
//        // ✅ FIXED: mock full Authentication
//        Authentication auth = mock(Authentication.class);
//        when(auth.getName()).thenReturn(userId);
//        SecurityContext securityContext = mock(SecurityContext.class);
//        when(securityContext.getAuthentication()).thenReturn(auth);
//        SecurityContextHolder.setContext(securityContext);
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(uploadImageFile.uploadImageFile(file)).thenReturn(uploadRes);
//        when(userRepository.save(any(User.class))).thenReturn(user);
//        when(userMapper.toUserResponse(user)).thenReturn(response);
//
//        UserResponse result = userService.updateUser(json, file);
//
//        assertEquals("Updated", result.getFirstName());
//        assertEquals("image.jpg", user.getAvatar().getUrl());
//    }
}
