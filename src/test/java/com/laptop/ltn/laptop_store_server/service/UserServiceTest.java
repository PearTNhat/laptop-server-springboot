package com.laptop.ltn.laptop_store_server.service;

import com.cloudinary.Cloudinary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laptop.ltn.laptop_store_server.dto.request.UpdateBlockRequest;
import com.laptop.ltn.laptop_store_server.dto.request.UpdateRoleRequest;
import com.laptop.ltn.laptop_store_server.dto.request.UserUpdateRequest;
import com.laptop.ltn.laptop_store_server.dto.request.WishListRequest;
import com.laptop.ltn.laptop_store_server.dto.response.UserResponse;
import com.laptop.ltn.laptop_store_server.entity.Image;
import com.laptop.ltn.laptop_store_server.entity.Product;
import com.laptop.ltn.laptop_store_server.entity.User;
import com.laptop.ltn.laptop_store_server.entity.WishListItem;
import com.laptop.ltn.laptop_store_server.exception.ErrorCode;
import com.laptop.ltn.laptop_store_server.mapper.UserMapper;
import com.laptop.ltn.laptop_store_server.repository.ProductRepository;
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

import java.util.ArrayList;
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
    @Mock
    ProductRepository productRepository;
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
    @Test
    void updateWishlist_shouldThrowException_whenProductIdIsNull() {
        WishListRequest request = WishListRequest.builder().product(null).build();

        assertThrows(IllegalArgumentException.class, () -> userService.updateWishlist(request));
    }

    @Test
    void updateWishlist_shouldRemoveProductIfExists() {
        String productId = "product123";
        WishListRequest request = WishListRequest.builder().product(productId).build();

        Product product = Product.builder()._id(productId).build();
        WishListItem item = new WishListItem(product);

        User user = new User();
        user.set_id("123");
        user.setWishlist(new ArrayList<>(List.of(item)));

        when(userRepository.findById("123")).thenReturn(Optional.of(user));

        userService.updateWishlist(request);

        assertTrue(user.getWishlist().isEmpty());
        verify(userRepository).save(user);
    }

    @Test
    void updateWishlist_shouldAddProductIfNotExists() {
        String productId = "product123";
        WishListRequest request = WishListRequest.builder().product(productId).build();

        User user = new User();
        user.set_id("123");
        user.setWishlist(new ArrayList<>());

        Product product = Product.builder()._id(productId).build();

        when(userRepository.findById("123")).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        userService.updateWishlist(request);

        assertEquals(1, user.getWishlist().size());
        verify(userRepository).save(user);
    }
    @Test
    void updateRole_shouldThrow_whenRoleOrUserIdMissing() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.updateRole(UpdateRoleRequest.builder().role("admin").build()));
    }

    @Test
    void updateRole_shouldThrow_whenInvalidRole() {
        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .userId("targetId")
                .role("admin")
                .build();

        assertThrows(RuntimeException.class, () -> userService.updateRole(request));
    }

    @Test
    void updateRole_shouldThrow_whenCurrentUserIsNotAdmin() {
        User currentUser = new User();
        currentUser.setRole("user");

        when(userRepository.findById("currentUserId")).thenReturn(Optional.of(currentUser));

        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .userId("targetId")
                .role("admin")
                .build();

        assertThrows(RuntimeException.class, () -> userService.updateRole(request));
    }

    @Test
    void updateRole_shouldUpdateSuccessfully() {
        User currentUser = new User();
        currentUser.set_id("123");
        currentUser.setRole("admin");

        User targetUser = new User();
        targetUser.set_id("targetId");

        when(userRepository.findById("123")).thenReturn(Optional.of(currentUser));
        when(userRepository.findById("targetId")).thenReturn(Optional.of(targetUser));

        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .userId("targetId")
                .role("user")
                .build();

        userService.updateRole(request);

        assertEquals("user", targetUser.getRole());
        verify(userRepository).save(targetUser);
    }

    @Test
    void updateBlock_shouldThrow_whenUserIdMissing() {
        UpdateBlockRequest request = UpdateBlockRequest.builder().userId(null).isBlocked(true).build();

        assertThrows(IllegalArgumentException.class, () -> userService.updateBlock(request));
    }

    @Test
    void updateBlock_shouldThrow_whenIsBlockedNull() {
        UpdateBlockRequest request = UpdateBlockRequest.builder().userId("targetId").isBlocked(null).build();

        assertThrows(IllegalArgumentException.class, () -> userService.updateBlock(request));
    }

    @Test
    void updateBlock_shouldThrow_whenNotAdmin() {
        User currentUser = new User();
        currentUser.setRole("user");

        when(userRepository.findById("currentUserId")).thenReturn(Optional.of(currentUser));

        UpdateBlockRequest request = UpdateBlockRequest.builder()
                .userId("targetId")
                .isBlocked(true)
                .build();

        assertThrows(SecurityException.class, () -> userService.updateBlock(request));
    }

    @Test
    void updateBlock_shouldUpdateSuccessfully() {
        User currentUser = new User();
        currentUser.setRole("admin");

        User targetUser = new User();

        when(userRepository.findById("123")).thenReturn(Optional.of(currentUser));
        when(userRepository.findById("targetId")).thenReturn(Optional.of(targetUser));

        UpdateBlockRequest request = UpdateBlockRequest.builder()
                .userId("targetId")
                .isBlocked(true)
                .build();

        userService.updateBlock(request);

        assertTrue(targetUser.isBlocked());
        verify(userRepository).save(targetUser);
    }


}
