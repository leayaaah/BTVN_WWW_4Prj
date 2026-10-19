package com.example.employeemanagement.service;

import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.Position;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.PositionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Employee chỉ lưu departmentId / positionId; lớp này gắn thêm tên phòng ban / chức vụ
 * (trường @Transient) để hiển thị trên View.
 */
@Component
public class EmployeeEnricher {

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    public EmployeeEnricher(DepartmentRepository departmentRepository, PositionRepository positionRepository) {
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
    }

    public List<Employee> enrich(List<Employee> employees) {
        Map<Integer, String> departments = departmentRepository.findAll().stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));
        Map<Integer, String> positions = positionRepository.findAll().stream()
                .collect(Collectors.toMap(Position::getId, Position::getTitle));
        for (Employee e : employees) {
            e.setDepartmentName(departments.get(e.getDepartmentId()));
            e.setPositionTitle(positions.get(e.getPositionId()));
        }
        return employees;
    }

    public Employee enrich(Employee employee) {
        enrich(List.of(employee));
        return employee;
    }
}
