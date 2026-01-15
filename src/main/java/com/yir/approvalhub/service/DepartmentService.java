package com.yir.approvalhub.service;

import com.yir.approvalhub.entity.Department;
import com.yir.approvalhub.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }

    @Transactional
    public Department createDepartment(Department department) {
        if (departmentRepository.existsByName(department.getName())) {
            throw new RuntimeException("Department name already exists");
        }
        return departmentRepository.save(department);
    }

    @Transactional
    public Department updateDepartment(Long id, Department departmentDetails) {
        Department department = getDepartmentById(id);

        if (!department.getName().equals(departmentDetails.getName()) 
                && departmentRepository.existsByName(departmentDetails.getName())) {
            throw new RuntimeException("Department name already exists");
        }

        department.setName(departmentDetails.getName());
        department.setDescription(departmentDetails.getDescription());
        department.setParent(departmentDetails.getParent());
        department.setEnabled(departmentDetails.getEnabled());

        return departmentRepository.save(department);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = getDepartmentById(id);
        departmentRepository.delete(department);
    }

    public List<Department> getEnabledDepartments() {
        return departmentRepository.findByEnabled(true);
    }
}
