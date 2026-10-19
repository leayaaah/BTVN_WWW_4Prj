package com.example.employeemanagement.repository;

import com.example.employeemanagement.dto.DepartmentEmployeeCount;
import com.example.employeemanagement.dto.DepartmentSalaryStat;
import com.example.employeemanagement.entity.Employee;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JDBC: ListCrudRepository (kế thừa CrudRepository) -> save, findById, findAll, deleteById.
 */
public interface EmployeeRepository extends ListCrudRepository<Employee, Integer> {

    // ----- Tìm kiếm (query derivation) -----
    List<Employee> findByNameContainingIgnoreCase(String name);

    List<Employee> findByAgeBetween(int from, int to);

    List<Employee> findBySalaryGreaterThan(double salary);

    // ----- Quan hệ 1 - n -----
    List<Employee> findByPositionId(Integer positionId);

    List<Employee> findByDepartmentIdAndPositionId(Integer departmentId, Integer positionId);

    @Query("SELECT * FROM employees WHERE department_id = :departmentId ORDER BY salary DESC, id LIMIT :limit")
    List<Employee> topPaidByDepartment(@Param("departmentId") Integer departmentId, @Param("limit") int limit);

    @Query(value = """
            SELECT d.id AS department_id, d.name AS department_name, COUNT(e.id) AS employee_count
            FROM departments d
            LEFT JOIN employees e ON e.department_id = d.id
            GROUP BY d.id, d.name
            ORDER BY d.id
            """, rowMapperClass = DepartmentEmployeeCountRowMapper.class)
    List<DepartmentEmployeeCount> countByDepartment();

    @Query(value = """
            SELECT d.id AS department_id, d.name AS department_name,
                   COALESCE(AVG(e.salary), 0) AS avg_salary, COALESCE(SUM(e.salary), 0) AS total_salary
            FROM departments d
            LEFT JOIN employees e ON e.department_id = d.id
            GROUP BY d.id, d.name
            ORDER BY d.id
            """, rowMapperClass = DepartmentSalaryStatRowMapper.class)
    List<DepartmentSalaryStat> salaryStatsByDepartment();
}
