package com.application_service.face.repository;

import com.application_service.entity.Face;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FaceRepository extends JpaRepository<Face, String> {
    Optional<Face> findByFaceOrder(Integer faceOrder);
}
