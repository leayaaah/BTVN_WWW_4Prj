package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.Department;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface DepartmentRepository extends MongoRepository<Department, Integer> {

    List<Department> findByIdNotIn(Collection<Integer> ids);
}
