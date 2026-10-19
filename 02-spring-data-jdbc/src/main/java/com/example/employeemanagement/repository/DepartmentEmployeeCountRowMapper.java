package com.example.employeemanagement.repository;

import com.example.employeemanagement.dto.DepartmentEmployeeCount;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/** RowMapper cho kết quả thống kê (không phải entity) của @Query. */
public class DepartmentEmployeeCountRowMapper implements RowMapper<DepartmentEmployeeCount> {

    @Override
    public DepartmentEmployeeCount mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new DepartmentEmployeeCount(
                rs.getInt("department_id"), rs.getString("department_name"), rs.getLong("employee_count"));
    }
}
