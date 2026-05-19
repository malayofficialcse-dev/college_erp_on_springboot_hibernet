package com.example.demo.dto.academic;

public record PromotionResponse(
        Long studentId,
        String studentName,
        Integer previousSemester,
        Integer newSemester,
        String status,
        Double cgpa,
        boolean promoted,
        String message
) {}
