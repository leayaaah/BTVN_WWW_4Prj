package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    // Department chưa có Employee nào
    @Query("""
            select d from Department d
            left join Employee e on e.department = d
            where e.id is null
            order by d.id
            """)
    List<Department> findWithoutEmployees();
}
