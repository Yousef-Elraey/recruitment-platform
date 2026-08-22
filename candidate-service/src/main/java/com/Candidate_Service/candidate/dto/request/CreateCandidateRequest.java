package com.Candidate_Service.candidate.dto.request;

import com.Candidate_Service.entity.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.Accessors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CreateCandidateRequest {
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
    private CandidateStatus status;

}
