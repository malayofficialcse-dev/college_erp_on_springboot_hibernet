package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.ERole;
import com.example.demo.model.User;
import com.example.demo.model.UserPermission;
import com.example.demo.repository.UserPermissionRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPermissionRepository userPermissionRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    @Transactional
    public User updateUser(Long id, User details) {
        User user = getUserById(id);
        user.setEnabled(details.isEnabled());
        if (details.getRoles() != null) {
            user.setRoles(details.getRoles());
        }
        if (details.getFullName() != null) {
            user.setFullName(details.getFullName());
        }
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userPermissionRepository.deleteByUserId(user.getId());
        userRepository.delete(user);
    }

    public List<UserPermission> getUserPermissions(Long userId) {
        getUserById(userId);
        return userPermissionRepository.findByUserId(userId);
    }

    @Transactional
    public List<UserPermission> updateUserPermissions(Long userId, List<UserPermission> permissions) {
        User user = getUserById(userId);
        
        List<UserPermission> savedPermissions = new ArrayList<>();
        for (UserPermission perm : permissions) {
            Optional<UserPermission> existingOpt = userPermissionRepository
                    .findByUserIdAndModuleName(userId, perm.getModuleName());
            
            UserPermission permissionToSave;
            if (existingOpt.isPresent()) {
                permissionToSave = existingOpt.get();
            } else {
                permissionToSave = new UserPermission();
                permissionToSave.setUser(user);
                permissionToSave.setModuleName(perm.getModuleName());
            }
            
            permissionToSave.setCanView(perm.isCanView());
            permissionToSave.setCanCreate(perm.isCanCreate());
            permissionToSave.setCanEdit(perm.isCanEdit());
            permissionToSave.setCanDelete(perm.isCanDelete());
            
            savedPermissions.add(userPermissionRepository.save(permissionToSave));
        }
        return savedPermissions;
    }

    @Transactional
    public void initUserPermissions(User user) {
        String[] modules = {
            "students", "employees", "departments", "payroll", "academics", 
            "library", "hostel", "transport", "finance", "notices"
        };
        
        for (String module : modules) {
            Optional<UserPermission> existing = userPermissionRepository
                    .findByUserIdAndModuleName(user.getId(), module);
            if (existing.isEmpty()) {
                UserPermission perm = new UserPermission();
                perm.setUser(user);
                perm.setModuleName(module);
                perm.setCanView(false);
                perm.setCanCreate(false);
                perm.setCanEdit(false);
                perm.setCanDelete(false);
                userPermissionRepository.save(perm);
            }
        }
    }
}
