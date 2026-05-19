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
        applyComputedFields(result);
        return examResultRepository.save(result);
    }

    @Transactional
    public ExamResult update(Long id, ExamResult details) {
        ExamResult result = getById(id);
        result.setStudent(details.getStudent());
        result.setExam(details.getExam());
        result.setSubject(details.getSubject());
        result.setMarksObtained(details.getMarksObtained());
        result.setTotalMarks(details.getTotalMarks());
        result.setSemester(details.getSemester());
        result.setRemarks(details.getRemarks());
        applyComputedFields(result);
        return examResultRepository.save(result);
    }

    @Transactional
    public void delete(Long id) {
        getById(id);
        examResultRepository.deleteById(id);
    }

    private void applyComputedFields(ExamResult result) {
        double gradePoint = calculateGradePoint(result.getMarksObtained(), result.getTotalMarks());
        result.setGradePoint(gradePoint);
        result.setGrade(calculateGradeFromPoint(gradePoint));
        if (result.getResultStatus() == null || result.getResultStatus().isBlank()) {
            double passingMarks = result.getExam() != null && result.getExam().getPassingMarks() != null
                    ? result.getExam().getPassingMarks()
                    : result.getTotalMarks() * 0.4;
            result.setResultStatus(result.getMarksObtained() >= passingMarks ? "PASS" : "FAIL");
        }
        if ("FAIL".equalsIgnoreCase(result.getResultStatus()) || "ABSENT".equalsIgnoreCase(result.getResultStatus())) {
            result.setGradePoint(0.0);
            result.setGrade("F");
        }
    }

    private double calculateGradePoint(double obtained, double total) {
        double percentage = (obtained / total) * 100;
        if (percentage >= 90) return 10.0;
        if (percentage >= 80) return 9.0;
        if (percentage >= 70) return 8.0;
        if (percentage >= 60) return 7.0;
        if (percentage >= 50) return 6.0;
        if (percentage >= 40) return 5.0;
        return 0.0;
    }

    private String calculateGradeFromPoint(double gradePoint) {
        if (gradePoint >= 10.0) return "O";
        if (gradePoint >= 9.0) return "A+";
        if (gradePoint >= 8.0) return "A";
        if (gradePoint >= 7.0) return "B+";
        if (gradePoint >= 6.0) return "B";
        if (gradePoint >= 5.0) return "C";
        return "F";
    }
}
