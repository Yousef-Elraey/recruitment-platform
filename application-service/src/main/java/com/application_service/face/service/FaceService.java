package com.application_service.face.service;

import com.application_service.common.exceprion.ErrorCode;
import com.application_service.common.exceprion.RecruitmentBusinessException;
import com.application_service.entity.Face;
import com.application_service.face.repository.FaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FaceService {
    private final FaceRepository faceRepository;

    public Face getByFaceCode(String faceCode) {
        Optional<Face> faceOp = faceRepository.findById(faceCode);
        if (faceOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.NO_FACE_FOUND.name(),
                    "face with code (" + faceCode + ") not found");
        }
        return faceOp.get();
    }

    public Face getByFaceOrder(Integer faceOrder) {
        Optional<Face> faceOrderOp = faceRepository.findByFaceOrder(faceOrder);
        if (faceOrderOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.NO_FACE_FOUND.name(),
                    "face with code (" + faceOrder + ") not found");
        }
        return faceOrderOp.get();
    }

}
