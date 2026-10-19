package com.example.employeemanagement.repository;

import com.example.employeemanagement.dto.EmployeeSearchCriteria;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.Position;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Spring JDBC thuần: dùng JdbcTemplate + RowMapper, không cần annotation entity.
 */
@Repository
public class EmployeeJdbcRepository {

    /** Câu SELECT dùng chung (JOIN để lấy luôn tên phòng ban và chức vụ). */
    public static final String SELECT_EMPLOYEE = """
            SELECT e.id, e.name, e.role, e.age, e.salary, e.department_id, e.position_id,
                   d.name AS department_name, p.title AS position_title
            FROM employees e
            LEFT JOIN departments d ON d.id = e.department_id
            LEFT JOIN positions p ON p.id = e.position_id
            """;

    public static final RowMapper<Employee> EMPLOYEE_MAPPER = (rs, rowNum) -> {
        Employee e = new Employee();
        e.setId(rs.getInt("id"));
        e.setName(rs.getString("name"));
        e.setRole(rs.getString("role"));
        e.setAge(rs.getObject("age", Integer.class));
        e.setSalary(rs.getObject("salary", Double.class));
        e.setDepartmentId(rs.getObject("department_id", Integer.class));
        e.setPositionId(rs.getObject("position_id", Integer.class));
        e.setDepartmentName(rs.getString("department_name"));
        e.setPositionTitle(rs.getString("position_title"));
        return e;
    };

    public static final RowMapper<Department> DEPARTMENT_MAPPER =
            (rs, rowNum) -> new Department(rs.getInt("id"), rs.getString("name"));

    public static final RowMapper<Position> POSITION_MAPPER =
            (rs, rowNum) -> new Position(rs.getInt("id"), rs.getString("title"));

    private final JdbcTemplate jdbc;

    public EmployeeJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ----- Xem danh sách: query("SELECT * ...", rowMapper) -----
    public List<Employee> findAll() {
        return jdbc.query(SELECT_EMPLOYEE + " ORDER BY e.id", EMPLOYEE_MAPPER);
    }

    // ----- Tìm theo id: queryForObject(... WHERE id=?) -----
    public Optional<Employee> findById(int id) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(SELECT_EMPLOYEE + " WHERE e.id = ?", EMPLOYEE_MAPPER, id));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    // ----- Tìm theo name / age / salary / position (có thể kết hợp) -----
    public List<Employee> search(EmployeeSearchCriteria c) {
        StringBuilder sql = new StringBuilder(SELECT_EMPLOYEE).append(" WHERE 1 = 1");
        List<Object> args = new ArrayList<>();

        if (c.getId() != null) {
            sql.append(" AND e.id = ?");
            args.add(c.getId());
        }
        if (StringUtils.hasText(c.getName())) {
            sql.append(" AND e.name LIKE ?");                    // WHERE name LIKE ?
            args.add("%" + c.getName().trim() + "%");
        }
        if (c.getMinAge() != null || c.getMaxAge() != null) {
            sql.append(" AND e.age BETWEEN ? AND ?");            // WHERE age BETWEEN ? AND ?
            args.add(c.getMinAge() != null ? c.getMinAge() : 0);
            args.add(c.getMaxAge() != null ? c.getMaxAge() : 200);
        }
        if (c.getMinSalary() != null) {
            sql.append(" AND e.salary > ?");                     // WHERE salary > ?
            args.add(c.getMinSalary());
        }
        if (c.getPositionId() != null) {
            sql.append(" AND e.position_id = ?");
            args.add(c.getPositionId());
        }
        sql.append(" ORDER BY e.id");
        return jdbc.query(sql.toString(), EMPLOYEE_MAPPER, args.toArray());
    }

    // ----- Thêm: update("INSERT ...") -----
    public int insert(Employee e) {
        String sql = "INSERT INTO employees (name, role, age, salary, department_id, position_id) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, e.getName());
            ps.setString(2, e.getRole());
            ps.setObject(3, e.getAge());
            ps.setObject(4, e.getSalary());
            ps.setObject(5, e.getDepartmentId());
            ps.setObject(6, e.getPositionId());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    // ----- Sửa: update("UPDATE ...") -----
    public int update(Employee e) {
        return jdbc.update(
                "UPDATE employees SET name = ?, role = ?, age = ?, salary = ?, department_id = ?, position_id = ? WHERE id = ?",
                e.getName(), e.getRole(), e.getAge(), e.getSalary(), e.getDepartmentId(), e.getPositionId(), e.getId());
    }

    // ----- Xóa: update("DELETE ... WHERE id=?") -----
    public int deleteById(int id) {
        return jdbc.update("DELETE FROM employees WHERE id = ?", id);
    }

    // ----- Danh mục dùng cho ô chọn -----
    public List<Department> findAllDepartments() {
        return jdbc.query("SELECT id, name FROM departments ORDER BY id", DEPARTMENT_MAPPER);
    }

    public List<Position> findAllPositions() {
        return jdbc.query("SELECT id, title FROM positions ORDER BY id", POSITION_MAPPER);
    }
}
