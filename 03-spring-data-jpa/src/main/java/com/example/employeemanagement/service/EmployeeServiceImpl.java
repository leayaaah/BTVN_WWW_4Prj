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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               DepartmentRepository departmentRepository,
                               PositionRepository positionRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll(Sort.by("id"));
    }

    @Override
    public List<Employee> search(EmployeeSearchCriteria c) {
        // Chọn một điều kiện để truy vấn bằng repository, các điều kiện còn lại lọc tiếp bằng c.matches()
        List<Employee> base;
        if (StringUtils.hasText(c.getName())) {
            base = employeeRepository.findByNameContainingIgnoreCase(c.getName().trim());
        } else if (c.getId() != null) {
            base = employeeRepository.findById(c.getId()).map(emp -> List.of(emp)).orElse(List.of());
        } else if (c.getMinAge() != null || c.getMaxAge() != null) {
            base = employeeRepository.findByAgeBetween(
                    c.getMinAge() != null ? c.getMinAge() : 0,
                    c.getMaxAge() != null ? c.getMaxAge() : 200);
        } else if (c.getMinSalary() != null) {
            base = employeeRepository.findBySalaryGreaterThan(c.getMinSalary());
        } else if (c.getPositionId() != null) {
            base = employeeRepository.findByPosition_Id(c.getPositionId());
        } else {
            base = employeeRepository.findAll(Sort.by("id"));
        }
        return base.stream().filter(c::matches).toList();
    }

    @Override
    public Employee findById(Integer id) {
        return employeeRepository.findById(id).orElseThrow(() -> notFound(id));
    }

    @Override
    public EmployeeForm getForm(Integer id) {
        return EmployeeForm.from(findById(id));
    }

    @Override
    @Transactional
    public Employee save(Integer id, EmployeeForm form) {
        Employee e = (id == null) ? new Employee() : findById(id);
        e.setName(form.getName().trim());
        e.setRole(form.getRole() == null || form.getRole().isBlank() ? null : form.getRole().trim());
        e.setAge(form.getAge());
        e.setSalary(form.getSalary().doubleValue());
        e.setDepartment(departmentRepository.findById(form.getDepartmentId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phòng ban không tồn tại")));
        e.setPosition(positionRepository.findById(form.getPositionId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chức vụ không tồn tại")));
        return employeeRepository.save(e);
    }

    @Override
    @Transactional
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

    private ResponseStatusException notFound(Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên có ID " + id);
    }
}
