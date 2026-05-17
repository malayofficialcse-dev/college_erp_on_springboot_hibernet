package com.example.demo.service;

import com.example.demo.model.ExamResult;
import com.example.demo.repository.ExamResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExamResultService {

    @Autowired
    private ExamResultRepository examResultRepository;

    public List<ExamResult> getAllExamResults() {
        return examResultRepository.findAll();
    }

    public ExamResult getExamResultById(Long id) {
        return examResultRepository.findById(id).orElse(null);
    }

    public ExamResult saveExamResult(ExamResult examResult) {
        return examResultRepository.save(examResult);
    }

    public ExamResult updateExamResult(Long id, ExamResult examResultDetails) {
        Optional<ExamResult> examResult = examResultRepository.findById(id);
        if (examResult.isPresent()) {
            ExamResult existing = examResult.get();
            existing.setStudent(examResultDetails.getStudent());
            existing.setSubject(examResultDetails.getSubject());
            existing.setMarksObtained(examResultDetails.getMarksObtained());
            existing.setTotalMarks(examResultDetails.getTotalMarks());
            existing.setGrade(examResultDetails.getGrade());
            existing.setSemester(examResultDetails.getSemester());
            return examResultRepository.save(existing);
        }
        return null;
    }

    public void deleteExamResult(Long id) {
        examResultRepository.deleteById(id);
    }
}
