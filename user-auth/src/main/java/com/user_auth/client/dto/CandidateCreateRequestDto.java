package com.user_auth.client.dto;

import com.user_auth.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CandidateCreateRequestDto {
    private Long id;
    private String name;
    @NotBlank(message = "phone is required")
    @Pattern(regexp = "^(\\+20|0)1[0-9]{9}$", message = "Invalid Egyptian phone number")
    private String phone;

    @NotBlank(message = "address is required")
    private String address;

    @NotBlank(message = "summary is required")
    private String summary;

    @NotNull(message = "status is required")
    private Status status;
}