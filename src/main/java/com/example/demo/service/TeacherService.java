package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Teacher;
import com.example.demo.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeacherService {

    @Autowired private TeacherRepository teacherRepository;

    public Page<Teacher> getAllTeachers(Pageable pageable) {
        return teacherRepository.findAll(pageable);
    }

    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", id));
    }

    public Page<Teacher> getByDepartment(Long deptId, Pageable pageable) {
        return teacherRepository.findByDepartmentId(deptId, pageable);
    }

    public Page<Teacher> getByStatus(String status, Pageable pageable) {
        return teacherRepository.findByStatus(status, pageable);
    }

    @Transactional
    public Teacher createTeacher(Teacher teacher) {
        if (teacherRepository.findByEmail(teacher.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + teacher.getEmail());
        }
        return teacherRepository.save(teacher);
    }

    @Transactional
    public Teacher updateTeacher(Long id, Teacher details) {
        Teacher teacher = getTeacherById(id);
        teacher.setFirstName(details.getFirstName());
        teacher.setLastName(details.getLastName());
        teacher.setPhone(details.getPhone());
        teacher.setDesignation(details.getDesignation());
        teacher.setQualification(details.getQualification());
        teacher.setStatus(details.getStatus());
        teacher.setDepartment(details.getDepartment());
        return teacherRepository.save(teacher);
    }

    @Transactional
    public void deleteTeacher(Long id) {
        getTeacherById(id);
        teacherRepository.deleteById(id);
    }
}
