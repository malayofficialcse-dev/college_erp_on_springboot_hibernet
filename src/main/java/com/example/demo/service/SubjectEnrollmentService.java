package com.example.demo.service;

import com.example.demo.dto.academic.SubjectEnrollmentRequest;
import com.example.demo.dto.academic.SubjectEnrollmentResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Student;
import com.example.demo.model.StudentSubjectEnrollment;
import com.example.demo.model.Subject;
import com.example.demo.repository.StudentSubjectEnrollmentRepository;
import com.example.demo.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class SubjectEnrollmentService {

    @Autowired private StudentService studentService;
    @Autowired private SubjectService subjectService;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private StudentSubjectEnrollmentRepository enrollmentRepository;

    public List<SubjectEnrollmentResponse> getEnrollments(Long studentId, Integer semesterNumber) {
        if (semesterNumber != null) {
            return enrollmentRepository.findByStudentIdAndSemesterNumberOrderByIdAsc(studentId, semesterNumber).stream()
                    .sorted((a, b) -> a.getSubject().getName().compareToIgnoreCase(b.getSubject().getName()))
                    .map(this::toResponse)
                    .toList();
        }
        return enrollmentRepository.findByStudentIdOrderBySemesterNumberAscIdAsc(studentId).stream()
                .sorted((a, b) -> {
                    int semesterCompare = a.getSemesterNumber().compareTo(b.getSemesterNumber());
                    if (semesterCompare != 0) {
                        return semesterCompare;
                    }
                    return a.getSubject().getName().compareToIgnoreCase(b.getSubject().getName());
                })
                .map(this::toResponse)
                .toList();
    }

    public List<Subject> getAvailableElectives(Long courseId, Integer semesterNumber) {
        return subjectRepository.findByCourseIdAndSemesterNumberAndSubjectTypeIgnoreCase(courseId, semesterNumber, "ELECTIVE");
    }

    @Transactional
    public List<SubjectEnrollmentResponse> enrollSubjects(SubjectEnrollmentRequest request, boolean electivesOnly) {
        Student student = studentService.getStudentById(request.studentId());
        Integer semesterNumber = request.semesterNumber() != null ? request.semesterNumber() : student.getCurrentSemester();
        if (semesterNumber == null) {
            throw new IllegalArgumentException("Semester number is required for subject enrollment.");
        }
        if (request.subjectIds() == null || request.subjectIds().isEmpty()) {
            throw new IllegalArgumentException("At least one subject must be provided.");
        }

        return request.subjectIds().stream().map(subjectId -> {
            Subject subject = subjectService.getSubjectById(subjectId);
            validateSubjectSelection(student, subject, semesterNumber, electivesOnly);
            StudentSubjectEnrollment enrollment = enrollmentRepository
                    .findByStudentIdAndSubjectIdAndSemesterNumber(student.getId(), subject.getId(), semesterNumber)
                    .orElseGet(StudentSubjectEnrollment::new);

            enrollment.setStudent(student);
            enrollment.setSubject(subject);
            enrollment.setSemesterNumber(semesterNumber);
            enrollment.setEnrollmentType(request.enrollmentType() != null && !request.enrollmentType().isBlank()
                    ? request.enrollmentType()
                    : electivesOnly ? "ELECTIVE" : "REGULAR");
            enrollment.setStatus("ENROLLED");
            enrollment.setEnrolledOn(LocalDate.now());
            return toResponse(enrollmentRepository.save(enrollment));
        }).toList();
    }

    @Transactional
    public void dropEnrollment(Long enrollmentId) {
        StudentSubjectEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentSubjectEnrollment", "id", enrollmentId));
        enrollmentRepository.deleteById(enrollmentId);
    }

    private void validateSubjectSelection(Student student, Subject subject, Integer semesterNumber, boolean electivesOnly) {
        if (subject.getCourse() == null || student.getCourses().stream().noneMatch(course -> Objects.equals(course.getId(), subject.getCourse().getId()))) {
            throw new IllegalArgumentException("Subject " + subject.getName() + " does not belong to the student's course.");
        }
        if (!Objects.equals(subject.getSemesterNumber(), semesterNumber)) {
            throw new IllegalArgumentException("Subject " + subject.getName() + " is not offered in semester " + semesterNumber + ".");
        }
        if (electivesOnly && !"ELECTIVE".equalsIgnoreCase(subject.getSubjectType())) {
            throw new IllegalArgumentException("Subject " + subject.getName() + " is not an elective.");
        }
    }

    private SubjectEnrollmentResponse toResponse(StudentSubjectEnrollment enrollment) {
        return new SubjectEnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudent().getId(),
                enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName(),
                enrollment.getSubject().getId(),
                enrollment.getSubject().getSubjectCode(),
                enrollment.getSubject().getName(),
                enrollment.getSemesterNumber(),
                enrollment.getEnrollmentType(),
                enrollment.getStatus(),
                enrollment.getEnrolledOn()
        );
    }
}
