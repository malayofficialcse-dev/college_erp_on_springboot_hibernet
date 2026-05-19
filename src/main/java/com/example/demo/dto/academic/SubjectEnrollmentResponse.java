package com.example.demo.dto.academic;

import java.time.LocalDate;

public record SubjectEnrollmentResponse(
        Long enrollmentId,
        Long studentId,
        String studentName,
        Long subjectId,
        String subjectCode,
        String subjectName,
        Integer semesterNumber,
        String enrollmentType,
        String status,
        LocalDate enrolledOn
) {}
