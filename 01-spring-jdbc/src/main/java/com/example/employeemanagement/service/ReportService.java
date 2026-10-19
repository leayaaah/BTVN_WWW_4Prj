package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.DepartmentEmployeeCount;
import com.example.employeemanagement.dto.DepartmentSalaryStat;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;

import java.util.List;

public interface ReportService {

    List<Employee> findByPosition(Integer positionId);

    List<Employee> findByDepartmentAndPosition(Integer departmentId, Integer positionId);

    List<DepartmentEmployeeCount> countEmployeesByDepartment();

    List<DepartmentSalaryStat> salaryStatsByDepartment();

    List<Employee> topPaidInDepartment(Integer departmentId, int n);

    List<Department> departmentsWithoutEmployees();
}
