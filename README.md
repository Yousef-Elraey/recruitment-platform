# Recruitment Management Platform

A microservices-based Recruitment Management Platform developed using **Spring Boot**.

The system provides functionality for user authentication, job management, candidate management, job applications, and AI-powered CV parsing.

---

## 📌 Project Overview

The Recruitment Management Platform is designed to manage the recruitment process from both the recruiter and candidate perspectives.

The system is divided into multiple independent microservices, where each service is responsible for a specific business domain.

### Main Features

- User registration and authentication
- JWT-based authentication and authorization
- Role-based access control
- Candidate management
- Job management
- Job application management
- Candidate tracking through recruitment stages
- CV upload and parsing
- AI-powered CV information extraction
- Local AI processing using Ollama
- Candidate and job validation between services
- Retry mechanism for failed CV parsing
- RESTful APIs
- MySQL database
- Microservices architecture

---

# 🏗️ System Architecture

The application is divided into the following services:

```text
                    ┌───────────────────────┐
                    │       Client          │
                    │   Frontend / Postman  │
                    └───────────┬───────────┘
                                │
                                │ JWT
                                ▼
                    ┌───────────────────────┐
                    │    User Auth Service  │
                    │                       │
                    │ - Registration        │
                    │ - Login               │
                    │ - JWT Generation      │
                    │ - User Management     │
                    └───────────┬───────────┘
                                │
                     JWT Token  │
                                │
          ┌─────────────────────┼──────────────────────┐
          │                     │                      │
          ▼                     ▼                      ▼
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────────┐
│  Candidate       │  │      Job         │  │   Application        │
│    Service       │  │    Service       │  │      Service         │
│                  │  │                  │  │                      │
│ - Candidates     │  │ - Jobs           │  │ - Applications       │
│ - CV Upload      │  │ - Job Management │  │ - Application Status  │
│ - CV Parsing     │  │ - Job Validation │  │ - Candidate Tracking │
│ - AI Parser      │  │                  │  │ - Job Validation     │
└────────┬─────────┘  └────────┬─────────┘  └──────────┬───────────┘
         │                     │                       │
         │                     │                       │
         ▼                     ▼                       ▼
   ┌───────────┐         ┌───────────┐          ┌───────────┐
   │   MySQL   │         │   MySQL   │          │   MySQL   │
   └───────────┘         └───────────┘          └───────────┘

                         Candidate Service
                                │
                                │ CV Text
                                ▼
                       ┌─────────────────┐
                       │     Ollama      │
                       │   Local LLM     │
                       │                 │
                       │  CV Parsing AI  │
                       └─────────────────┘