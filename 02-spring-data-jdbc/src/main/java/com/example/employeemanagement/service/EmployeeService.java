package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeForm;
import com.example.employeemanagement.dto.EmployeeSearchCriteria;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.Position;

import java.util.List;

public interface EmployeeService {

    List<Employee> findAll();

    List<Employee> search(EmployeeSearchCriteria criteria);

    Employee findById(Integer id);

    EmployeeForm getForm(Integer id);

    /** id == null: thêm mới; id != null: cập nhật. */
    Employee save(Integer id, EmployeeForm form);

    void delete(Integer id);

    List<Department> findAllDepartments();

    List<Position> findAllPositions();
}
