package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeForm;
import com.example.employeemanagement.dto.EmployeeSearchCriteria;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.Position;
import com.example.employeemanagement.repository.EmployeeJdbcRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeJdbcRepository repository;

    public EmployeeServiceImpl(EmployeeJdbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Employee> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Employee> search(EmployeeSearchCriteria criteria) {
        return repository.search(criteria);
    }

    @Override
    public Employee findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> notFound(id));
    }

    @Override
    public EmployeeForm getForm(Integer id) {
        return EmployeeForm.from(findById(id));
    }

    @Override
    public Employee save(Integer id, EmployeeForm form) {
        Employee e = new Employee();
        e.setName(form.getName().trim());
        e.setRole(form.getRole() == null || form.getRole().isBlank() ? null : form.getRole().trim());
        e.setAge(form.getAge());
        e.setSalary(form.getSalary().doubleValue());
        e.setDepartmentId(form.getDepartmentId());
        e.setPositionId(form.getPositionId());

        int savedId;
        if (id == null) {
            savedId = repository.insert(e);
        } else {
            e.setId(id);
            if (repository.update(e) == 0) {
                throw notFound(id);
            }
            savedId = id;
        }
        return findById(savedId);
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public List<Department> findAllDepartments() {
        return repository.findAllDepartments();
    }

    @Override
    public List<Position> findAllPositions() {
        return repository.findAllPositions();
    }

    private ResponseStatusException notFound(Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên có ID " + id);
    }
}
