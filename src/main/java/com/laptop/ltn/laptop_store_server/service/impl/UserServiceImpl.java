package com.laptop.ltn.laptop_store_server.service.impl;

import com.laptop.ltn.laptop_store_server.dto.response.UserResponse;
import com.laptop.ltn.laptop_store_server.entity.User;
import com.laptop.ltn.laptop_store_server.exception.AppException;
import com.laptop.ltn.laptop_store_server.exception.ErrorCode;
import com.laptop.ltn.laptop_store_server.mapper.UserMapper;
import com.laptop.ltn.laptop_store_server.repository.UserRepository;
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

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    public UserResponse getUserInfo() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED)));
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
