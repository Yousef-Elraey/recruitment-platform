package com.Candidate_Service.cv.dto.response;

import com.Candidate_Service.entity.CvStatus;
import com.Candidate_Service.entity.CvVersion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.function.LongFunction;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UploadCvResponse {
    private Long cvId;
    private Long cvInfoId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
    private CvStatus status;
    private CvVersion version;
}
