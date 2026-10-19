package com.example.employeemanagement.repository;

import com.example.employeemanagement.dto.DepartmentSalaryStat;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DepartmentSalaryStatRowMapper implements RowMapper<DepartmentSalaryStat> {

    @Override
    public DepartmentSalaryStat mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new DepartmentSalaryStat(
                rs.getInt("department_id"), rs.getString("department_name"),
                rs.getDouble("avg_salary"), rs.getDouble("total_salary"));
    }
}
