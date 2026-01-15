package com.yir.approvalhub.repository;

import com.yir.approvalhub.entity.Department;
import com.yir.approvalhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByRoleAndEnabled(User.UserRole role, Boolean enabled);
    List<User> findByRole(User.UserRole role);
    List<User> findByDepartment(Department department);
    List<User> findByEnabled(Boolean enabled);
}
