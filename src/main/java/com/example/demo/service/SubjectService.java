package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Subject;
import com.example.demo.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubjectService {

    @Autowired private SubjectRepository subjectRepository;

    public Page<Subject> getAllSubjects(Pageable pageable) {
        return subjectRepository.findAll(pageable);
    }

    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", id));
    }

    public Page<Subject> getByCourse(Long courseId, Pageable pageable) {
        return subjectRepository.findByCourseId(courseId, pageable);
    }

    public Page<Subject> getByCourseAndSemester(Long courseId, Integer semester, Pageable pageable) {
        return subjectRepository.findByCourseIdAndSemesterNumber(courseId, semester, pageable);
    }

    public Page<Subject> getByTeacher(Long teacherId, Pageable pageable) {
        return subjectRepository.findByTeacherId(teacherId, pageable);
    }

    public Page<Subject> search(Long courseId, Long teacherId, Integer semesterNumber, String keyword, Pageable pageable) {
        return subjectRepository.search(courseId, teacherId, semesterNumber, emptyToNull(keyword), pageable);
    }

    @Transactional
    public Subject createSubject(Subject subject) {
        if (subjectRepository.findBySubjectCode(subject.getSubjectCode()).isPresent()) {
            throw new IllegalArgumentException("Subject code already exists: " + subject.getSubjectCode());
        }
        return subjectRepository.save(subject);
    }

    @Transactional
    public Subject updateSubject(Long id, Subject details) {
        Subject subject = getSubjectById(id);
        subject.setName(details.getName());
        subject.setDescription(details.getDescription());
        subject.setCredits(details.getCredits());
        subject.setSemesterNumber(details.getSemesterNumber());
        subject.setSubjectType(details.getSubjectType());
        subject.setCourse(details.getCourse());
        subject.setTeacher(details.getTeacher());
        return subjectRepository.save(subject);
    }

    @Transactional
    public void deleteSubject(Long id) {
        getSubjectById(id);
        subjectRepository.deleteById(id);
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
