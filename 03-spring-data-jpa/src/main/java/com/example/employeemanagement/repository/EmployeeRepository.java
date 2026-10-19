package com.example.employeemanagement.repository;

import com.example.employeemanagement.dto.DepartmentEmployeeCount;
import com.example.employeemanagement.dto.DepartmentSalaryStat;
import com.example.employeemanagement.entity.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA: JpaRepository -> save, findById, findAll, deleteById ...
 */
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    // ----- Tìm kiếm (query derivation) -----
    List<Employee> findByNameContainingIgnoreCase(String name);

    List<Employee> findByAgeBetween(int from, int to);

    List<Employee> findBySalaryGreaterThan(double salary);

    // ----- Quan hệ 1 - n (đi qua thuộc tính department.id / position.id) -----
    List<Employee> findByPosition_Id(Integer positionId);

    List<Employee> findByDepartment_IdAndPosition_Id(Integer departmentId, Integer positionId);

    // Top N: truyền PageRequest.of(0, n)
    List<Employee> findByDepartment_IdOrderBySalary_Id(Integer departmentId, Pageable pageable);

    // ----- Thống kê (JPQL + DTO) -----
    @Query("""
            select new com.example.employeemanagement.dto.DepartmentEmployeeCount(d.id, d.name, count(e))
            from Department d left join Employee e on e.department = d
            group by d.id, d.name
            order by d.id
            """)
    List<DepartmentEmployeeCount> countByDepartment();

    @Query("""
            select new com.example.employeemanagement.dto.DepartmentSalaryStat(d.id, d.name, avg(e.salary), sum(e.salary))
            from Department d left join Employee e on e.department = d
            group by d.id, d.name
            order by d.id
            """)
    List<DepartmentSalaryStat> salaryStatsByDepartment();
}
