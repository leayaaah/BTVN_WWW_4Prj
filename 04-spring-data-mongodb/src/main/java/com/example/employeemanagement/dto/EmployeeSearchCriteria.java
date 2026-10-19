package com.example.employeemanagement.dto;

import com.example.employeemanagement.entity.Employee;
import org.springframework.util.StringUtils;

/**
 * Điều kiện tìm kiếm Employee theo id, name, age, salary, position.
 * Các điều kiện có thể kết hợp với nhau.
 */
public class EmployeeSearchCriteria {

    private Integer id;
    private String name;
    private Integer minAge;
    private Integer maxAge;
    private Long minSalary;      // tìm lương LỚN HƠN giá trị này
    private Integer positionId;

    public boolean hasAny() {
        return id != null || StringUtils.hasText(name) || minAge != null || maxAge != null
                || minSalary != null || positionId != null;
    }

    /**
     * Kiểm tra các điều kiện còn lại (trừ name - name luôn được lọc ở tầng database
     * bằng findByNameContainingIgnoreCase / LIKE để dùng cùng collation với database).
     */
    public boolean matches(Employee e) {
        if (id != null && !id.equals(e.getId())) return false;
        if (minAge != null && (e.getAge() == null || e.getAge() < minAge)) return false;
        if (maxAge != null && (e.getAge() == null || e.getAge() > maxAge)) return false;
        if (minSalary != null && (e.getSalary() == null || e.getSalary() <= minSalary)) return false;
        if (positionId != null && !positionId.equals(e.getPositionId())) return false;
        return true;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getMinAge() { return minAge; }
    public void setMinAge(Integer minAge) { this.minAge = minAge; }

    public Integer getMaxAge() { return maxAge; }
    public void setMaxAge(Integer maxAge) { this.maxAge = maxAge; }

    public Long getMinSalary() { return minSalary; }
    public void setMinSalary(Long minSalary) { this.minSalary = minSalary; }

    public Integer getPositionId() { return positionId; }
    public void setPositionId(Integer positionId) { this.positionId = positionId; }
}
