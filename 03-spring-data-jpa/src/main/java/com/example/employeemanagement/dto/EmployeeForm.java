package com.example.employeemanagement.dto;

import com.example.employeemanagement.entity.Employee;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class EmployeeForm {

    private Integer id;

    @NotBlank(message = "Họ tên không được để trống")
    private String name;

    private String role;

    @NotNull(message = "Vui lòng nhập tuổi")
    @Min(value = 18, message = "Tuổi tối thiểu là 18")
    @Max(value = 70, message = "Tuổi tối đa là 70")
    private Integer age;

    @NotNull(message = "Vui lòng nhập lương")
    @Positive(message = "Lương phải lớn hơn 0")
    private Long salary;

    @NotNull(message = "Vui lòng chọn phòng ban")
    private Integer departmentId;

    @NotNull(message = "Vui lòng chọn chức vụ")
    private Integer positionId;

    public static EmployeeForm from(Employee e) {
        EmployeeForm f = new EmployeeForm();
        f.id = e.getId();
        f.name = e.getName();
        f.role = e.getRole();
        f.age = e.getAge();
        f.salary = e.getSalary() == null ? null : Math.round(e.getSalary());
        f.departmentId = e.getDepartmentId();
        f.positionId = e.getPositionId();
        return f;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Long getSalary() { return salary; }
    public void setSalary(Long salary) { this.salary = salary; }

    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }

    public Integer getPositionId() { return positionId; }
    public void setPositionId(Integer positionId) { this.positionId = positionId; }
}
