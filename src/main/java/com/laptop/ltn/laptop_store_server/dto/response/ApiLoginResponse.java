package com.laptop.ltn.laptop_store_server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// Những filed nào null thì nó k trả về
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiLoginResponse<T> {
    @Builder.Default
    Boolean success =true;
    String accessToken;
    T userData;
    String message;
}
