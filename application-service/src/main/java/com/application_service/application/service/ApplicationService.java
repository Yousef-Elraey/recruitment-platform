package com.application_service.application.service;

import com.application_service.application.dto.request.AddFeedbackScoreRequest;
import com.application_service.application.dto.request.AssignRecruiterIdRequest;
import com.application_service.application.dto.request.CreateApplicationRequest;
import com.application_service.application.dto.request.NextFaceRequest;
import com.application_service.application.dto.response.AddFeedbackScoreResponse;
import com.application_service.application.dto.response.AssignRecruiterIdResponse;
import com.application_service.application.dto.response.CreateApplicationResponse;
import com.application_service.application.repository.ApplicationRepository;
import com.application_service.candidate_face.dto.response.GetCandidateFaceResponse;
import com.application_service.candidate_face.repository.CandidateFaceRepository;
import com.application_service.client.*;
import com.application_service.common.exceprion.ErrorCode;
import com.application_service.common.exceprion.RecruitmentBusinessException;
import com.application_service.entity.Application;
import com.application_service.entity.CandidateFace;
import com.application_service.entity.Face;
import com.application_service.face.repository.FaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final CandidateFaceRepository candidateFaceRepository;
    private final FaceRepository faceRepository;
    private final CandidateClient candidateClient;
    private final JobClient jobClient;
    private final UserClient userClient;
    public CreateApplicationResponse createApplication(CreateApplicationRequest request) {
        Long candidateId = request.getCandidateId();
        Long jobId = request.getJobId();

       CandidateResponseDto candidateResponseDto = candidateClient.getCandidate(candidateId);
        JobResponseDto jobResponseDto = jobClient.getJob(jobId);

       Optional<Application> applicationOp = applicationRepository.
                findByCandidateIdAndJobId(candidateId, jobId);
        if (applicationOp.isPresent()) {
            throw new RecruitmentBusinessException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS.name(),
                    "application which its candidate_id (" + candidateId + ") and job_id (" + jobId + ") is already existed");
        }
        Application application = new Application();
        application.setCandidateId(candidateId)
                .setJobId(jobId)
                .setFace(new Face("APPLY", 1));
        applicationRepository.save(application);

        CandidateFace candidateFace = new CandidateFace();
        candidateFace
                .setApplication(application)
                .setFace(application.getFace());
        candidateFaceRepository.save(candidateFace);


        CreateApplicationResponse response = new CreateApplicationResponse();
        response.setId(application.getId())
                .setCandidateId(application.getCandidateId())
                .setJobId(application.getJobId())
                .setFaceCode(application.getFace().getFaceCode())
                .setCreatedAt(application.getCreatedAt())
                .setCreatedBy(application.getCreatedBy());

        return response;

    }


    public AssignRecruiterIdResponse assignRecruiterId(AssignRecruiterIdRequest request) {
       UserResponseDto user = userClient.getUser(request.getRecruiterId());
            if (user.getRole().equals("CANDIDATE")){
                throw new RecruitmentBusinessException(HttpStatus.CONFLICT,ErrorCode.NOT_VALID_OPERATION.name(),
                        "the recruiter cannot be candidate");
            }

        Optional<Application> applicationOp = applicationRepository.findById(request.getApplicationId());
        if (applicationOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.APPLICATION_NOT_FOUND.name(),
                    "Application with id (" + request.getApplicationId() + ") not found");
        }
        Application application = applicationOp.get();

        if (application.getRecruiterId() != null) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_ACCEPTABLE, ErrorCode.ALREADY_EXISTS.name(),
                    "there is already recruiter assigned to this application");
        }
        application.setRecruiterId(request.getRecruiterId());
        applicationRepository.save(application);
        AssignRecruiterIdResponse response = new AssignRecruiterIdResponse();
        response.setRecruiterId(application.getRecruiterId());
        return response;
    }

    public AddFeedbackScoreResponse addFinalFeedbackScore(AddFeedbackScoreRequest request) {
        Optional<Application> applicationOp = applicationRepository.findById(request.getApplicationId());
        if (applicationOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.APPLICATION_NOT_FOUND.name(),
                    "Application with id (" + request.getApplicationId() + ") not found");
        }
        Application application = applicationOp.get();
        application.setFeedback(request.getFeedback())
                .setScore(request.getScore());
        applicationRepository.save(application);

        AddFeedbackScoreResponse response = new AddFeedbackScoreResponse();
        response.setFeedback(application.getFeedback())
                .setScore(application.getScore());
        return response;

    }

    public GetCandidateFaceResponse nextFace(NextFaceRequest request) {
        Optional<Application> applicationOp = applicationRepository.findById(request.getApplicationId());
        if (applicationOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.APPLICATION_NOT_FOUND.name(),
                    "application with id (" + request.getApplicationId() + ") not found");
        }
        Application application = applicationOp.get();

        if (application.getFace().getFaceCode().equals("REJECTED")){
            throw new RecruitmentBusinessException(HttpStatus.CONFLICT,ErrorCode.NOT_VALID_OPERATION.name(),
                    "this application is REJECTED");
        }

        Face oldFace = application.getFace();
        Integer oldFaceOrder = oldFace.getFaceOrder();
        Integer newFaceOrder = ++oldFaceOrder;

        Optional<Face> newFaceOp = faceRepository.findByFaceOrder(newFaceOrder);
        if (newFaceOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.NO_FACE_FOUND.name(),
                    "no faces after this stage");
        }
        Face newFace = newFaceOp.get();
        AddFeedbackScoreRequest addFeedbackScoreRequest = new AddFeedbackScoreRequest();
        addFeedbackScoreRequest.setApplicationId(request.getApplicationId())
                .setFeedback(request.getFeedback())
                .setScore(request.getScore());

        if (newFace.getFaceCode().equals("INTERVIEW")) {
            addFinalFeedbackScore(addFeedbackScoreRequest);
        }

        application.setFace(newFace);
        applicationRepository.save(application);

        CandidateFace candidateFace = new CandidateFace();
        candidateFace
                .setApplication(application)
                .setFace(newFace);
        candidateFaceRepository.save(candidateFace);

        GetCandidateFaceResponse response = new GetCandidateFaceResponse();
        response.setId(candidateFace.getId())
                .setApplicationId(candidateFace.getApplication().getId())
                .setFaceCode(candidateFace.getFace().getFaceCode())
                .setUpdatedBy(candidateFace.getUpdatedBy())
                .setUpdatedAt(candidateFace.getUpdatedAt());
        return response;

    }

    public void rejectApplication(Long applicationId) {
        Optional<Application> applicationOp = applicationRepository.findById(applicationId);
        if (applicationOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.APPLICATION_NOT_FOUND.name(),
                    "application with id (" + applicationId + ") not found");
        }
        Application application = applicationOp.get();

        if (application.getFace().getFaceCode().equals("APPROVE")){
        throw new RecruitmentBusinessException(HttpStatus.CONFLICT,ErrorCode.NOT_VALID_OPERATION.name(),
                "cannot reject this application after already approved");
        }

        application.setFace(faceRepository.findById("REJECTED").get());
        applicationRepository.save(application);

    }
}
