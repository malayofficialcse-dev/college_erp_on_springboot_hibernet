package com.example.demo.dto.academic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record HallTicketResponse(
        Long studentId,
        String studentName,
        String enrollmentNumber,
        String department,
        String course,
        Integer semesterNumber,
        String examName,
        String examType,
        List<ExamPaper> papers,
        List<String> instructions
) {
    public record ExamPaper(
            Long scheduleId,
            String subjectCode,
            String subjectName,
            LocalDate examDate,
            LocalTime startTime,
            LocalTime endTime,
            String roomNumber,
            String seatNumber
    ) {}
}
