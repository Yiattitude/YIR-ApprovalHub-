package com.yir.approvalhub.config;

import com.yir.approvalhub.entity.Department;
import com.yir.approvalhub.entity.Position;
import com.yir.approvalhub.entity.User;
import com.yir.approvalhub.repository.DepartmentRepository;
import com.yir.approvalhub.repository.PositionRepository;
import com.yir.approvalhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (userRepository.count() > 0) {
            return;
        }

        // Create departments
        Department itDept = new Department();
        itDept.setName("IT部门");
        itDept.setDescription("信息技术部门");
        itDept.setEnabled(true);
        itDept = departmentRepository.save(itDept);

        Department hrDept = new Department();
        hrDept.setName("人力资源部");
        hrDept.setDescription("人力资源管理部门");
        hrDept.setEnabled(true);
        hrDept = departmentRepository.save(hrDept);

        Department financeDept = new Department();
        financeDept.setName("财务部");
        financeDept.setDescription("财务管理部门");
        financeDept.setEnabled(true);
        financeDept = departmentRepository.save(financeDept);

        // Create positions
        Position employeePos = new Position();
        employeePos.setName("员工");
        employeePos.setDescription("普通员工");
        employeePos.setLevel(1);
        employeePos.setEnabled(true);
        employeePos = positionRepository.save(employeePos);

        Position managerPos = new Position();
        managerPos.setName("部门经理");
        managerPos.setDescription("部门管理人员");
        managerPos.setLevel(2);
        managerPos.setEnabled(true);
        managerPos = positionRepository.save(managerPos);

        Position hrManagerPos = new Position();
        hrManagerPos.setName("HR经理");
        hrManagerPos.setDescription("人力资源经理");
        hrManagerPos.setLevel(2);
        hrManagerPos.setEnabled(true);
        hrManagerPos = positionRepository.save(hrManagerPos);

        Position adminPos = new Position();
        adminPos.setName("系统管理员");
        adminPos.setDescription("系统管理员");
        adminPos.setLevel(3);
        adminPos.setEnabled(true);
        adminPos = positionRepository.save(adminPos);

        // Create admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFullName("系统管理员");
        admin.setEmail("admin@yir.com");
        admin.setPhone("13800000000");
        admin.setDepartment(itDept);
        Set<Position> adminPositions = new HashSet<>();
        adminPositions.add(adminPos);
        admin.setPositions(adminPositions);
        admin.setRole(User.UserRole.ADMIN);
        admin.setEnabled(true);
        userRepository.save(admin);

        // Create approver users
        User approver1 = new User();
        approver1.setUsername("manager1");
        approver1.setPassword(passwordEncoder.encode("manager123"));
        approver1.setFullName("张经理");
        approver1.setEmail("manager1@yir.com");
        approver1.setPhone("13800000001");
        approver1.setDepartment(itDept);
        Set<Position> manager1Positions = new HashSet<>();
        manager1Positions.add(managerPos);
        approver1.setPositions(manager1Positions);
        approver1.setRole(User.UserRole.APPROVER);
        approver1.setEnabled(true);
        userRepository.save(approver1);

        User approver2 = new User();
        approver2.setUsername("hrmanager");
        approver2.setPassword(passwordEncoder.encode("hr123"));
        approver2.setFullName("李HR");
        approver2.setEmail("hr@yir.com");
        approver2.setPhone("13800000002");
        approver2.setDepartment(hrDept);
        Set<Position> hrPositions = new HashSet<>();
        hrPositions.add(hrManagerPos);
        approver2.setPositions(hrPositions);
        approver2.setRole(User.UserRole.APPROVER);
        approver2.setEnabled(true);
        userRepository.save(approver2);

        // Create employee users
        User employee1 = new User();
        employee1.setUsername("employee1");
        employee1.setPassword(passwordEncoder.encode("emp123"));
        employee1.setFullName("王员工");
        employee1.setEmail("emp1@yir.com");
        employee1.setPhone("13800000003");
        employee1.setDepartment(itDept);
        Set<Position> emp1Positions = new HashSet<>();
        emp1Positions.add(employeePos);
        employee1.setPositions(emp1Positions);
        employee1.setRole(User.UserRole.EMPLOYEE);
        employee1.setEnabled(true);
        userRepository.save(employee1);

        User employee2 = new User();
        employee2.setUsername("employee2");
        employee2.setPassword(passwordEncoder.encode("emp123"));
        employee2.setFullName("赵员工");
        employee2.setEmail("emp2@yir.com");
        employee2.setPhone("13800000004");
        employee2.setDepartment(hrDept);
        Set<Position> emp2Positions = new HashSet<>();
        emp2Positions.add(employeePos);
        employee2.setPositions(emp2Positions);
        employee2.setRole(User.UserRole.EMPLOYEE);
        employee2.setEnabled(true);
        userRepository.save(employee2);

        System.out.println("===================================");
        System.out.println("Sample data initialized successfully!");
        System.out.println("Admin: admin/admin123");
        System.out.println("Manager1: manager1/manager123");
        System.out.println("HR Manager: hrmanager/hr123");
        System.out.println("Employee1: employee1/emp123");
        System.out.println("Employee2: employee2/emp123");
        System.out.println("===================================");
    }
}
