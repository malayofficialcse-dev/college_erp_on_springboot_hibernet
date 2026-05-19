package com.example.demo.repository;

import com.example.demo.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    Page<Book> findByCategory(String category, Pageable pageable);
    Page<Book> findByDepartmentId(Long departmentId, Pageable pageable);
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Book> findByAvailableCopiesGreaterThan(Integer copies, Pageable pageable);
    List<Book> findByAvailableCopiesLessThanEqual(Integer threshold);

    @Query("SELECT b.category, COUNT(b) FROM Book b GROUP BY b.category")
    List<Object[]> countByCategory();

    @Query("SELECT COALESCE(b.department.name, 'UNASSIGNED'), COUNT(b) FROM Book b GROUP BY b.department.name")
    List<Object[]> countByDepartment();
}
