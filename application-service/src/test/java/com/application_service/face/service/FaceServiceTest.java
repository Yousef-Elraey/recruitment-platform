package com.application_service.face.service;

import static org.junit.jupiter.api.Assertions.*;

import com.application_service.common.exceprion.ErrorCode;
import com.application_service.common.exceprion.RecruitmentBusinessException;
import com.application_service.entity.Face;
import com.application_service.face.repository.FaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FaceServiceTest {

    @Mock
    private FaceRepository faceRepository;

    @InjectMocks
    private FaceService faceService;

    private Face face;

    @BeforeEach
    void setUp() {
        face = new Face("APPLY", 1);
    }


    @Test
    void getByFaceCode_shouldThrowException_whenNotFound() {
        when(faceRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> faceService.getByFaceCode("UNKNOWN")
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.NO_FACE_FOUND.name(), exception.getErrorCode());
        assertTrue(exception.getMessage().contains("UNKNOWN"));
    }

    @Test
    void getByFaceCode_shouldReturnFace_whenFound() {
        when(faceRepository.findById("APPLY")).thenReturn(Optional.of(face));

        Face result = faceService.getByFaceCode("APPLY");

        assertEquals(face, result);
        assertEquals("APPLY", result.getFaceCode());
    }

    @Test
    void getByFaceOrder_shouldThrowException_whenNotFound() {
        when(faceRepository.findByFaceOrder(99)).thenReturn(Optional.empty());

        RecruitmentBusinessException exception = assertThrows(
                RecruitmentBusinessException.class,
                () -> faceService.getByFaceOrder(99)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(ErrorCode.NO_FACE_FOUND.name(), exception.getErrorCode());
        assertTrue(exception.getMessage().contains("99"));
    }

    @Test
    void getByFaceOrder_shouldReturnFace_whenFound() {
        when(faceRepository.findByFaceOrder(1)).thenReturn(Optional.of(face));

        Face result = faceService.getByFaceOrder(1);

        assertEquals(face, result);
        assertEquals(1, result.getFaceOrder());
    }
}