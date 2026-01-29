package com.uzum.transactionprocessing.dto.error;

import com.uzum.transactionprocessing.constant.enums.ErrorType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorDto {
    int code;
    String message;
    ErrorType type;
    List<String>validationErrors;
}
