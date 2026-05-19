package com.example.demo.dto.academic;

import java.util.List;

public record AcademicPerformanceResponse(
        Long studentId,
        String studentName,
        Double gpa,
        Double cgpa,
        List<SemesterPerformance> semesters
) {
    public record SemesterPerformance(
            String semester,
            Double gpa,
            Double creditsEarned,
            Double totalCredits,
            List<ResultItem> results
    ) {}

    public record ResultItem(
            Long resultId,
            String subjectCode,
            String subjectName,
            Integer credits,
            Double marksObtained,
            Double totalMarks,
            String grade,
            Double gradePoint,
            String resultStatus
    ) {}
}
