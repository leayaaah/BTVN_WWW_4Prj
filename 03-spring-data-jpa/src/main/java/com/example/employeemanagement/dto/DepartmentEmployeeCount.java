package com.example.employeemanagement.dto;

/** Kết quả thống kê: số Employee của mỗi Department. */
public class DepartmentEmployeeCount {

    private final Integer departmentId;
    private final String departmentName;
    private final long employeeCount;

    public DepartmentEmployeeCount(Integer departmentId, String departmentName, Long employeeCount) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.employeeCount = employeeCount == null ? 0L : employeeCount;
    }

    public Integer getDepartmentId() { return departmentId; }
    public String getDepartmentName() { return departmentName; }
    public long getEmployeeCount() { return employeeCount; }
}
