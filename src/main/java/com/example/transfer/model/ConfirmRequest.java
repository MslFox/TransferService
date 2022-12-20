package com.example.transfer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.example.transfer.model.ErrorMessage.*;

@Data
@Builder
@AllArgsConstructor
public class ConfirmRequest {
    @NotNull(message = NULL_OPERATION_ID)
    @Pattern(regexp = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}",
            message = WRONG_OPERATION_ID_FORMAT)
    private String operationId;
    private String code;

}
