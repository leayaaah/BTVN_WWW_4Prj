package com.example.employeemanagement.repository;

import com.example.employeemanagement.dto.DepartmentEmployeeCount;
import com.example.employeemanagement.dto.DepartmentSalaryStat;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.employeemanagement.repository.EmployeeJdbcRepository.DEPARTMENT_MAPPER;
import static com.example.employeemanagement.repository.EmployeeJdbcRepository.EMPLOYEE_MAPPER;
import static com.example.employeemanagement.repository.EmployeeJdbcRepository.SELECT_EMPLOYEE;

/**
 * Các truy vấn theo quan hệ 1 - n bằng SQL thuần + RowMapper.
 */
@Repository
public class ReportJdbcRepository {

    private final JdbcTemplate jdbc;

    public ReportJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Position 1-n Employee: WHERE position_id = ?
    public List<Employee> findByPosition(int positionId) {
        return jdbc.query(SELECT_EMPLOYEE + " WHERE e.position_id = ? ORDER BY e.id",
                EMPLOYEE_MAPPER, positionId);
    }

    // WHERE department_id = ? AND position_id = ?
    public List<Employee> findByDepartmentAndPosition(int departmentId, int positionId) {
        return jdbc.query(SELECT_EMPLOYEE + " WHERE e.department_id = ? AND e.position_id = ? ORDER BY e.id",
                EMPLOYEE_MAPPER, departmentId, positionId);
    }

    // Thống kê số Employee của mỗi Department (LEFT JOIN để phòng ban chưa có nhân viên vẫn hiện số 0)
    public List<DepartmentEmployeeCount> countEmployeesByDepartment() {
        String sql = """
                SELECT d.id, d.name, COUNT(e.id) AS employee_count
                FROM departments d
                LEFT JOIN employees e ON e.department_id = d.id
                GROUP BY d.id, d.name
                ORDER BY d.id
                """;
        return jdbc.query(sql, (rs, rowNum) -> new DepartmentEmployeeCount(
                rs.getInt("id"), rs.getString("name"), rs.getLong("employee_count")));
    }

    // Lương trung bình và tổng lương theo Department
    public List<DepartmentSalaryStat> salaryStatsByDepartment() {
        String sql = """
                SELECT d.id, d.name, AVG(e.salary) AS avg_salary, SUM(e.salary) AS total_salary
                FROM departments d
                LEFT JOIN employees e ON e.department_id = d.id
                GROUP BY d.id, d.name
                ORDER BY d.id
                """;
        return jdbc.query(sql, (rs, rowNum) -> new DepartmentSalaryStat(
                rs.getInt("id"), rs.getString("name"), rs.getDouble("avg_salary"), rs.getDouble("total_salary")));
    }

    // Top N Employee lương cao nhất trong một Department
    public List<Employee> topPaidInDepartment(int departmentId, int n) {
        return jdbc.query(SELECT_EMPLOYEE + " WHERE e.department_id = ? ORDER BY e.salary DESC, e.id LIMIT ?",
                EMPLOYEE_MAPPER, departmentId, n);
    }

    // Danh sách Department chưa có Employee nào
    public List<Department> departmentsWithoutEmployees() {
        String sql = """
                SELECT d.id, d.name
                FROM departments d
                LEFT JOIN employees e ON e.department_id = d.id
                WHERE e.id IS NULL
                ORDER BY d.id
                """;
        return jdbc.query(sql, DEPARTMENT_MAPPER);
    }
}
