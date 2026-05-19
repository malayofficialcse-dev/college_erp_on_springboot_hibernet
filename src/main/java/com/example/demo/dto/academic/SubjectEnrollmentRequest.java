package com.example.demo.dto.academic;

import java.util.List;

public record SubjectEnrollmentRequest(
        Long studentId,
        Integer semesterNumber,
        List<Long> subjectIds,
        String enrollmentType
) {}
