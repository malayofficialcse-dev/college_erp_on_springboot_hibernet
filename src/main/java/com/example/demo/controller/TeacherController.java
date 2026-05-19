package com.example.demo.controller;

import com.example.demo.model.Teacher;
import com.example.demo.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "*")
public class TeacherController {

    @Autowired private TeacherService teacherService;

    @GetMapping
    public ResponseEntity<Page<Teacher>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(teacherService.getAllTeachers(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }

    @GetMapping("/department/{deptId}")
    public ResponseEntity<Page<Teacher>> getByDepartment(@PathVariable Long deptId,
                                                          @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(teacherService.getByDepartment(deptId, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Teacher>> search(@RequestParam(required = false) Long departmentId,
                                                @RequestParam(required = false) String status,
                                                @RequestParam(required = false) String keyword,
                                                @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(teacherService.search(departmentId, status, keyword, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Teacher> create(@Valid @RequestBody Teacher teacher) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.createTeacher(teacher));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Teacher> update(@PathVariable Long id, @RequestBody Teacher details) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, details));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }
}
