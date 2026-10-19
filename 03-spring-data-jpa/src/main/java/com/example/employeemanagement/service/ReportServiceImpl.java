package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.DepartmentEmployeeCount;
import com.example.employeemanagement.dto.DepartmentSalaryStat;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public ReportServiceImpl(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<Employee> findByPosition(Integer positionId) {
        return employeeRepository.findByPosition_Id(positionId);
    }

    @Override
    public List<Employee> findByDepartmentAndPosition(Integer departmentId, Integer positionId) {
        return employeeRepository.findByDepartment_IdAndPosition_Id(departmentId, positionId);
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
        return employeeRepository.findByDepartment_IdOrderBySalary_Id(departmentId, PageRequest.of(0, n));
    }

    @Override
    public List<Department> departmentsWithoutEmployees() {
        return departmentRepository.findWithoutEmployees();
    }
}
