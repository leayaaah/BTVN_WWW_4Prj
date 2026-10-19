package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.Department;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface DepartmentRepository extends ListCrudRepository<Department, Integer> {

    @Query("""
            SELECT d.* FROM departments d
            LEFT JOIN employees e ON e.department_id = d.id
            WHERE e.id IS NULL
            ORDER BY d.id
            """)
    List<Department> findWithoutEmployees();
}
