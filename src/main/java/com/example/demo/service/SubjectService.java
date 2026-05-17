package com.example.demo.service;

import com.example.demo.model.Subject;
import com.example.demo.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id).orElse(null);
    }

    public Subject saveSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    public Subject updateSubject(Long id, Subject subjectDetails) {
        Optional<Subject> subject = subjectRepository.findById(id);
        if (subject.isPresent()) {
            Subject existing = subject.get();
            existing.setName(subjectDetails.getName());
            existing.setCode(subjectDetails.getCode());
            existing.setCredits(subjectDetails.getCredits());
            existing.setCourse(subjectDetails.getCourse());
            existing.setTeacher(subjectDetails.getTeacher());
            return subjectRepository.save(existing);
        }
        return null;
    }

    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }
}
