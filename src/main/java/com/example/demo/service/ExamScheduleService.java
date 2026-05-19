package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.ExamSchedule;
import com.example.demo.repository.ExamScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExamScheduleService {

    @Autowired private ExamScheduleRepository examScheduleRepository;

    public Page<ExamSchedule> getAll(Pageable pageable) {
        return examScheduleRepository.findAll(pageable);
    }

    public ExamSchedule getById(Long id) {
        return examScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExamSchedule", "id", id));
    }

    public Page<ExamSchedule> getByExam(Long examId, Pageable pageable) {
        return examScheduleRepository.findByExamId(examId, pageable);
    }

    public Page<ExamSchedule> getByCourseAndSemester(Long courseId, Integer semesterNumber, Pageable pageable) {
        return examScheduleRepository.findByExamCourseIdAndSubjectSemesterNumber(courseId, semesterNumber, pageable);
    }

    public List<ExamSchedule> getByDateRange(LocalDate from, LocalDate to) {
        return examScheduleRepository.findByExamDateBetweenOrderByExamDateAscStartTimeAsc(from, to);
    }

    @Transactional
    public ExamSchedule create(ExamSchedule schedule) {
        return examScheduleRepository.save(schedule);
    }

    @Transactional
    public ExamSchedule update(Long id, ExamSchedule details) {
        ExamSchedule existing = getById(id);
        existing.setExam(details.getExam());
        existing.setSubject(details.getSubject());
        existing.setExamDate(details.getExamDate());
        existing.setStartTime(details.getStartTime());
        existing.setEndTime(details.getEndTime());
        existing.setRoomNumber(details.getRoomNumber());
        existing.setSeatNumberPrefix(details.getSeatNumberPrefix());
        existing.setInstructions(details.getInstructions());
        return examScheduleRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        getById(id);
        examScheduleRepository.deleteById(id);
    }
}
