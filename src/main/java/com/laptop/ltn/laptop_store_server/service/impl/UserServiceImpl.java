package com.laptop.ltn.laptop_store_server.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laptop.ltn.laptop_store_server.dto.request.UpdateBlockRequest;
import com.laptop.ltn.laptop_store_server.dto.request.UpdateRoleRequest;
import com.laptop.ltn.laptop_store_server.dto.request.UserUpdateRequest;
import com.laptop.ltn.laptop_store_server.dto.request.WishListRequest;
import com.laptop.ltn.laptop_store_server.dto.response.UserResponse;
import com.laptop.ltn.laptop_store_server.entity.Image;
import com.laptop.ltn.laptop_store_server.entity.User;
import com.laptop.ltn.laptop_store_server.entity.WishListItem;
import com.laptop.ltn.laptop_store_server.exception.AppException;
import com.laptop.ltn.laptop_store_server.exception.ErrorCode;
import com.laptop.ltn.laptop_store_server.mapper.UserMapper;
import com.laptop.ltn.laptop_store_server.repository.ProductRepository;
import com.laptop.ltn.laptop_store_server.repository.UserRepository;
import com.laptop.ltn.laptop_store_server.service.UploadImageFile;
import com.laptop.ltn.laptop_store_server.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    MongoTemplate mongoTemplate;
    UserRepository userRepository;
    UserMapper userMapper;
    UploadImageFile uploadImageFile;
    Cloudinary cloudinary;
    private final ProductRepository productRepository;

    public UserResponse getUserInfo() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    @Override
    public Page<User> findAllWithFilters(Map<String, String> queryParams, Pageable pageable) {
        Query query = new Query();

        Set<String> excludeFields = Set.of("page", "sort", "limit", "fields", "search");
        Map<String, String> filters = queryParams.entrySet().stream()
                .filter(e -> !excludeFields.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Advanced filtering
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Kiểm tra nếu key có dạng field[operator]
            Pattern pattern = Pattern.compile("(.+)\\[(gte|gt|lte|lt|ne)\\]");
            Matcher matcher = pattern.matcher(key);
            if (matcher.matches()) {
                String field = matcher.group(1);
                String operator = matcher.group(2);

                Criteria criteria = switch (operator) {
                    case "gte" -> Criteria.where(field).gte(castValue(value));
                    case "gt" -> Criteria.where(field).gt(castValue(value));
                    case "lte" -> Criteria.where(field).lte(castValue(value));
                    case "lt" -> Criteria.where(field).lt(castValue(value));
                    case "ne" -> Criteria.where(field).ne(castValue(value));
                    default -> Criteria.where(field).is(castValue(value));
                };
                query.addCriteria(criteria);
            } else {
                query.addCriteria(Criteria.where(key).is(castValue(value)));
            }
        }

        // Tìm kiếm search
        if (queryParams.containsKey("search")) {
            String search = queryParams.get("search");
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("firstName").regex(search, "i"),
                    Criteria.where("lastName").regex(search, "i"),
                    Criteria.where("email").regex(search, "i")
                    // Nếu muốn tìm theo fullName, bạn cần tạo trường fullName tạm thời từ firstName + lastName
            );
            query.addCriteria(searchCriteria);
        }

        long total = mongoTemplate.count(query, User.class);
        List<User> users = mongoTemplate.find(query.with(pageable), User.class);
        return new org.springframework.data.domain.PageImpl<>(users, pageable, total);
    }

    @Override
    public UserResponse updateUser(String documentJson, MultipartFile file) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserUpdateRequest request = null;
        try {
            request = objectMapper.readValue(documentJson, UserUpdateRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (StringUtils.isEmpty(request.getFirstName()) &&
                StringUtils.isEmpty(request.getLastName()) &&
                StringUtils.isEmpty(request.getPhone()) &&
                StringUtils.isEmpty(request.getEmail())) {
            throw new RuntimeException("Missing inputs");
        }
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Image uploadedImage = null;
        if (file != null && !file.isEmpty()) {
            try {
                // Upload to Cloudinary
                Map res = uploadImageFile.uploadImageFile(file);
                uploadedImage = Image.builder()
                        .url((String) res.get("url"))
                        .public_id((String) res.get("public_id"))
                        .build();


                // Delete old avatar if exists
                if (user.getAvatar() != null && user.getAvatar().getPublic_id() != null) {

                    cloudinary.uploader().destroy(user.getAvatar().getPublic_id(), Map.of());
                }
                user.setAvatar(uploadedImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // Cập nhật thông tin người dùng
        user.setFirstName(request.getFirstName() != null ? request.getFirstName() : user.getFirstName());
        user.setLastName(request.getLastName() != null ? request.getLastName() : user.getLastName());
        user.setEmail(request.getEmail() != null ? request.getEmail() : user.getEmail());
        user.setPhone(request.getPhone() != null ? request.getPhone() : user.getPhone());
        user.setAddress(request.getAddress() != null ? request.getAddress() : user.getAddress());
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public void updateWishlist(WishListRequest request) {
        String productId = request.getProduct();
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID must not be null or empty");
        }
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean alreadyExists = user.getWishlist().stream()
                .anyMatch(item -> item.getProduct().get_id().equals(productId));
        if (alreadyExists) {
            user.getWishlist().removeIf(item -> item.getProduct().get_id().equals(productId));
        } else {
            user.getWishlist().add(new WishListItem(productRepository.findById(productId).orElseThrow(()-> new RuntimeException("Product not found"))));
        }

        userRepository.save(user);
    }
    @Override
    public void updateRole(UpdateRoleRequest request) {
        String newRole = request.getRole();
        String targetUserId = request.getUserId();
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (newRole == null || targetUserId == null) {
            throw new IllegalArgumentException("Missing role or userId");
        }
        if (!newRole.equals("user") && !newRole.equals("admin")) {
            throw new IllegalArgumentException("Role must be 'user' or 'admin'");
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        if (!"admin".equals(currentUser.getRole())) {
            throw new SecurityException("You are not admin");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        targetUser.setRole(newRole);
        userRepository.save(targetUser);
    }

    @Override
    public void updateBlock(UpdateBlockRequest request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("Missing input");
        }
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        Boolean isBlocked = request.getIsBlocked();
        if (isBlocked == null) {
            throw new IllegalArgumentException("Blocking must be true or false");
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new SecurityException("Current user not found"));

        if (!"admin".equals(currentUser.getRole())) {
            throw new SecurityException("You are not admin");
        }
        User targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Target user not found"));
        targetUser.setBlocked(isBlocked);
        System.out.println(isBlocked);
        userRepository.save(targetUser);
    }

    private Object castValue(String value) {
        if (value.matches("^\\d+$")) {
            return Integer.parseInt(value);
        } else if (value.matches("^\\d+\\.\\d+$")) {
            return Double.parseDouble(value);
        } else if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return Boolean.parseBoolean(value);
        }
        return value;
    }
}
