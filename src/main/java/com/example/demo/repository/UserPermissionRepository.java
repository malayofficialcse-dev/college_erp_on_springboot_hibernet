package com.example.demo.repository;

import com.example.demo.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    List<UserPermission> findByUserId(Long userId);
    Optional<UserPermission> findByUserIdAndModuleName(Long userId, String moduleName);
    void deleteByUserId(Long userId);
}
