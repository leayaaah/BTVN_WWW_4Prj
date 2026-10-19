package com.example.employeemanagement.config;

import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.Position;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.repository.PositionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Nạp dữ liệu mẫu vào database NoSQL "employeewww" từ departments.json và positions.json
 * (chỉ chạy khi collection còn trống).
 *
 * departments.json lưu nhân viên lồng bên trong từng phòng ban; ở đây tách ra thành 3 collection riêng:
 * positions, departments, employees (employees giữ departmentId và positionId).
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final ObjectMapper objectMapper;
    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DataLoader(ObjectMapper objectMapper,
                      PositionRepository positionRepository,
                      DepartmentRepository departmentRepository,
                      EmployeeRepository employeeRepository) {
        this.objectMapper = objectMapper;
        this.positionRepository = positionRepository;
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(String... args) throws IOException {
        if (positionRepository.count() == 0) {
            List<Position> positions = new ArrayList<>();
            for (JsonNode node : read("data/positions.json")) {
                positions.add(new Position(node.get("_id").asInt(), node.get("title").asText()));
            }
            positionRepository.saveAll(positions);
        }

        boolean needDepartments = departmentRepository.count() == 0;
        boolean needEmployees = employeeRepository.count() == 0;
        if (!needDepartments && !needEmployees) {
            return;
        }

        List<Department> departments = new ArrayList<>();
        List<Employee> employees = new ArrayList<>();
        for (JsonNode d : read("data/departments.json")) {
            int departmentId = d.get("_id").asInt();
            departments.add(new Department(departmentId, d.get("name").asText()));
            for (JsonNode n : d.path("employees")) {
                Employee e = new Employee();
                e.setId(n.get("employeeId").asInt());
                e.setName(n.get("name").asText());
                e.setRole(n.path("role").isNull() ? null : n.path("role").asText(null));
                e.setAge(n.path("age").isNumber() ? n.get("age").asInt() : null);
                e.setSalary(n.path("salary").isNumber() ? n.get("salary").asDouble() : null);
                e.setDepartmentId(departmentId);
                e.setPositionId(n.path("positionId").isNumber() ? n.get("positionId").asInt() : null);
                employees.add(e);
            }
        }
        if (needDepartments) {
            departmentRepository.saveAll(departments);
        }
        if (needEmployees) {
            employeeRepository.saveAll(employees);
        }
    }

    private JsonNode read(String path) throws IOException {
        try (InputStream in = new ClassPathResource(path).getInputStream()) {
            return objectMapper.readTree(in);
        }
    }
}
