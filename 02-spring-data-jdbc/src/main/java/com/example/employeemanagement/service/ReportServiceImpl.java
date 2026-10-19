package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.DepartmentEmployeeCount;
import com.example.employeemanagement.dto.DepartmentSalaryStat;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeEnricher enricher;

    public ReportServiceImpl(EmployeeRepository employeeRepository,
                             DepartmentRepository departmentRepository,
                             EmployeeEnricher enricher) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.enricher = enricher;
    }

    @Override
    public List<Employee> findByPosition(Integer positionId) {
        return enricher.enrich(new ArrayList<>(employeeRepository.findByPositionId(positionId)));
    }

    @Override
    public List<Employee> findByDepartmentAndPosition(Integer departmentId, Integer positionId) {
        return enricher.enrich(new ArrayList<>(
                employeeRepository.findByDepartmentIdAndPositionId(departmentId, positionId)));
    }

    @Override
    public List<DepartmentEmployeeCount> countEmployeesByDepartment() {
        return employeeRepository.countByDepartment();
    }

    @Override
    public List<DepartmentSalaryStat> salaryStatsByDepartment() {
        return employeeRepository.salaryStatsByDepartment();
    }

    @Override
    public List<Employee> topPaidInDepartment(Integer departmentId, int n) {
        return enricher.enrich(new ArrayList<>(employeeRepository.topPaidByDepartment(departmentId, n)));
    }

    @Override
    public List<Department> departmentsWithoutEmployees() {
        return departmentRepository.findWithoutEmployees();
    }
}
