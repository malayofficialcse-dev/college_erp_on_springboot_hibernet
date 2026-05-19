package com.example.demo.repository;

import com.example.demo.model.Timetable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    List<Timetable> findByCourseIdAndDayOfWeek(Long courseId, String dayOfWeek);
    Page<Timetable> findByCourseId(Long courseId, Pageable pageable);
    Page<Timetable> findByTeacherId(Long teacherId, Pageable pageable);
    Page<Timetable> findBySemesterId(Long semesterId, Pageable pageable);
}
