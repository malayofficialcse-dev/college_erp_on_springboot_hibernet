package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Timetable;
import com.example.demo.repository.TimetableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TimetableService {

    @Autowired private TimetableRepository timetableRepository;

    public Page<Timetable> getAll(Pageable pageable) {
        return timetableRepository.findAll(pageable);
    }

    public Timetable getById(Long id) {
        return timetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable", "id", id));
    }

    public List<Timetable> getByCourseAndDay(Long courseId, String dayOfWeek) {
        return timetableRepository.findByCourseIdAndDayOfWeek(courseId, dayOfWeek);
    }

    public Page<Timetable> getByTeacher(Long teacherId, Pageable pageable) {
        return timetableRepository.findByTeacherId(teacherId, pageable);
    }

    @Transactional
    public Timetable create(Timetable timetable) {
        return timetableRepository.save(timetable);
    }

    @Transactional
    public Timetable update(Long id, Timetable details) {
        Timetable existing = getById(id);
        existing.setDayOfWeek(details.getDayOfWeek());
        existing.setStartTime(details.getStartTime());
        existing.setEndTime(details.getEndTime());
        existing.setRoomNumber(details.getRoomNumber());
        existing.setCourse(details.getCourse());
        existing.setSubject(details.getSubject());
        existing.setTeacher(details.getTeacher());
        return timetableRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        getById(id);
        timetableRepository.deleteById(id);
    }
}
