package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.DepartmentEmployeeCount;
import com.example.employeemanagement.dto.DepartmentSalaryStat;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.repository.ReportJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportJdbcRepository repository;

    public ReportServiceImpl(ReportJdbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Employee> findByPosition(Integer positionId) {
        return repository.findByPosition(positionId);
    }

    @Override
    public List<Employee> findByDepartmentAndPosition(Integer departmentId, Integer positionId) {
        return repository.findByDepartmentAndPosition(departmentId, positionId);
    }

    @Override
    public List<DepartmentEmployeeCount> countEmployeesByDepartment() {
        return repository.countEmployeesByDepartment();
    }

    @Override
    public List<DepartmentSalaryStat> salaryStatsByDepartment() {
        return repository.salaryStatsByDepartment();
    }

    @Override
    public List<Employee> topPaidInDepartment(Integer departmentId, int n) {
        return repository.topPaidInDepartment(departmentId, n);
    }

    @Override
    public List<Department> departmentsWithoutEmployees() {
        return repository.departmentsWithoutEmployees();
    }
}
