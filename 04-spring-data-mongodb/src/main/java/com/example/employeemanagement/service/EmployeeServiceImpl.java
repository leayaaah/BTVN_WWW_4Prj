package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeForm;
import com.example.employeemanagement.dto.EmployeeSearchCriteria;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.Position;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.repository.PositionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final MongoTemplate mongoTemplate;
    private final EmployeeEnricher enricher;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               DepartmentRepository departmentRepository,
                               PositionRepository positionRepository,
                               MongoTemplate mongoTemplate,
                               EmployeeEnricher enricher) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.mongoTemplate = mongoTemplate;
        this.enricher = enricher;
    }

    @Override
    public List<Employee> findAll() {
        return enricher.enrich(new ArrayList<>(employeeRepository.findAll(Sort.by("id"))));
    }

    @Override
    public List<Employee> search(EmployeeSearchCriteria c) {
        // Chọn một điều kiện để truy vấn, các điều kiện còn lại lọc tiếp bằng c.matches()
        List<Employee> base;
        if (StringUtils.hasText(c.getName())) {
            base = employeeRepository.findByNameContainingIgnoreCase(c.getName().trim());
        } else if (c.getId() != null) {
            base = employeeRepository.findById(c.getId()).map(emp -> List.of(emp)).orElse(List.of());
        } else if (c.getMinAge() != null || c.getMaxAge() != null) {
            // MongoTemplate + Criteria.gte / lte
            Criteria age = Criteria.where("age")
                    .gte(c.getMinAge() != null ? c.getMinAge() : 0)
                    .lte(c.getMaxAge() != null ? c.getMaxAge() : 200);
            base = mongoTemplate.find(Query.query(age), Employee.class);
        } else if (c.getMinSalary() != null) {
            base = employeeRepository.findBySalaryGreaterThan(c.getMinSalary());
        } else if (c.getPositionId() != null) {
            base = employeeRepository.findByPositionId(c.getPositionId());
        } else {
            base = employeeRepository.findAll();
        }
        List<Employee> result = new ArrayList<>(base.stream()
                .filter(c::matches)
                .sorted((a, b) -> Integer.compare(a.getId(), b.getId()))
                .toList());
        return enricher.enrich(result);
    }

    @Override
    public Employee findById(Integer id) {
        Employee e = employeeRepository.findById(id).orElseThrow(() -> notFound(id));
        return enricher.enrich(e);
    }

    @Override
    public EmployeeForm getForm(Integer id) {
        return EmployeeForm.from(findById(id));
    }

    @Override
    public Employee save(Integer id, EmployeeForm form) {
        Employee e;
        if (id == null) {
            e = new Employee();
            e.setId(nextId());
        } else {
            e = employeeRepository.findById(id).orElseThrow(() -> notFound(id));
        }
        e.setName(form.getName().trim());
        e.setRole(form.getRole() == null || form.getRole().isBlank() ? null : form.getRole().trim());
        e.setAge(form.getAge());
        e.setSalary(form.getSalary().doubleValue());
        e.setDepartmentId(form.getDepartmentId());
        e.setPositionId(form.getPositionId());
        return enricher.enrich(employeeRepository.save(e));
    }

    @Override
    public void delete(Integer id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public List<Department> findAllDepartments() {
        return departmentRepository.findAll(Sort.by("id"));
    }

    @Override
    public List<Position> findAllPositions() {
        return positionRepository.findAll(Sort.by("id"));
    }

    private int nextId() {
        return employeeRepository.findTopByOrderByIdDesc().map(Employee::getId).orElse(0) + 1;
    }

    private ResponseStatusException notFound(Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên có ID " + id);
    }
}
