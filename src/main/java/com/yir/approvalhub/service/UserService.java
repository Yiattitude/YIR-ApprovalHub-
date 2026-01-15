package com.yir.approvalhub.service;

import com.yir.approvalhub.dto.ApiResponse;
import com.yir.approvalhub.entity.Department;
import com.yir.approvalhub.entity.Position;
import com.yir.approvalhub.entity.User;
import com.yir.approvalhub.repository.DepartmentRepository;
import com.yir.approvalhub.repository.PositionRepository;
import com.yir.approvalhub.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                     DepartmentRepository departmentRepository,
                     PositionRepository positionRepository,
                     PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);

        if (!user.getUsername().equals(userDetails.getUsername()) 
                && userRepository.existsByUsername(userDetails.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        user.setUsername(userDetails.getUsername());
        user.setFullName(userDetails.getFullName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        user.setRole(userDetails.getRole());
        user.setEnabled(userDetails.getEnabled());

        if (userDetails.getDepartment() != null) {
            Department department = departmentRepository.findById(userDetails.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            user.setDepartment(department);
        }

        if (userDetails.getPositions() != null && !userDetails.getPositions().isEmpty()) {
            Set<Position> positions = new HashSet<>();
            for (Position pos : userDetails.getPositions()) {
                Position position = positionRepository.findById(pos.getId())
                        .orElseThrow(() -> new RuntimeException("Position not found: " + pos.getId()));
                positions.add(position);
            }
            user.setPositions(positions);
        }

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public List<User> getUsersByDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        return userRepository.findByDepartment(department);
    }

    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    public List<User> getEnabledUsers() {
        return userRepository.findByEnabled(true);
    }
}
