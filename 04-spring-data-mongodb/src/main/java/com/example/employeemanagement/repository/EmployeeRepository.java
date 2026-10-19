package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB: MongoRepository -> save, findById, findAll, deleteById ...
 */
public interface EmployeeRepository extends MongoRepository<Employee, Integer> {

    // ----- Tìm kiếm (query derivation) -----
    List<Employee> findByNameContainingIgnoreCase(String name);       // regex, không phân biệt hoa/thường

    List<Employee> findBySalaryGreaterThan(double salary);

    // (Tìm theo age dùng MongoTemplate + Criteria.gte/lte trong service, vì Between của Mongo không gồm 2 đầu mút)

    // ----- Quan hệ 1 - n (tham chiếu bằng departmentId / positionId) -----
    List<Employee> findByPositionId(Integer positionId);

    List<Employee> findByDepartmentIdAndPositionId(Integer departmentId, Integer positionId);

    // Top N: truyền PageRequest.of(0, n)
    List<Employee> findByDepartmentIdOrderBySalaryDesc(Integer departmentId, Pageable pageable);

    // Mongo không có auto-increment nên cần lấy id lớn nhất để sinh id mới
    Optional<Employee> findTopByOrderByIdDesc();
}
