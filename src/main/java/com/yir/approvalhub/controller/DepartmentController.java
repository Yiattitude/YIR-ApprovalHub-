package com.yir.approvalhub.controller;

import com.yir.approvalhub.dto.ApiResponse;
import com.yir.approvalhub.entity.Department;
import com.yir.approvalhub.service.DepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllDepartments() {
        try {
            List<Department> departments = departmentService.getAllDepartments();
            return ResponseEntity.ok(new ApiResponse(true, "Success", departments));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDepartmentById(@PathVariable Long id) {
        try {
            Department department = departmentService.getDepartmentById(id);
            return ResponseEntity.ok(new ApiResponse(true, "Success", department));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createDepartment(@RequestBody Department department) {
        try {
            Department createdDepartment = departmentService.createDepartment(department);
            return ResponseEntity.ok(new ApiResponse(true, "Department created successfully", createdDepartment));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        try {
            Department updatedDepartment = departmentService.updateDepartment(id, department);
            return ResponseEntity.ok(new ApiResponse(true, "Department updated successfully", updatedDepartment));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.ok(new ApiResponse(true, "Department deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
