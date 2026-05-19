package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.ExamResult;
import com.example.demo.repository.ExamResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExamResultService {

    @Autowired private ExamResultRepository examResultRepository;

    public Page<ExamResult> getAllResults(Pageable pageable) {
        return examResultRepository.findAll(pageable);
    }

    public ExamResult getById(Long id) {
        return examResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExamResult", "id", id));
    }

    public Page<ExamResult> getByStudent(Long studentId, Pageable pageable) {
        return examResultRepository.findByStudentId(studentId, pageable);
    }

    public Page<ExamResult> getByExam(Long examId, Pageable pageable) {
        return examResultRepository.findByExamId(examId, pageable);
    }

    public Page<ExamResult> getByStudentAndSemester(Long studentId, String semester, Pageable pageable) {
        return examResultRepository.findByStudentIdAndSemester(studentId, semester, pageable);
    }

    @Transactional
    public ExamResult create(ExamResult result) {
        // Auto-calculate grade if not provided
        if (result.getGrade() == null || result.getGrade().isEmpty()) {
            result.setGrade(calculateGrade(result.getMarksObtained(), result.getTotalMarks()));
        }
        return examResultRepository.save(result);
    }

    @Transactional
    public ExamResult update(Long id, ExamResult details) {
        ExamResult result = getById(id);
        result.setMarksObtained(details.getMarksObtained());
        result.setGrade(calculateGrade(details.getMarksObtained(), result.getTotalMarks()));
        result.setResultStatus(details.getResultStatus());
        result.setRemarks(details.getRemarks());
        return examResultRepository.save(result);
    }

    @Transactional
    public void delete(Long id) {
        getById(id);
        examResultRepository.deleteById(id);
    }

    private String calculateGrade(double obtained, double total) {
        double percentage = (obtained / total) * 100;
        if (percentage >= 90) return "O";
        if (percentage >= 80) return "A+";
        if (percentage >= 70) return "A";
        if (percentage >= 60) return "B+";
        if (percentage >= 50) return "B";
        if (percentage >= 40) return "C";
        return "F";
    }
}
