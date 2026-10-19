package com.example.employeemanagement.dto;

/** Kết quả thống kê: lương trung bình và tổng lương theo Department. */
public class DepartmentSalaryStat {

    private final Integer departmentId;
    private final String departmentName;
    private final double avgSalary;
    private final double totalSalary;

    public DepartmentSalaryStat(Integer departmentId, String departmentName, Double avgSalary, Double totalSalary) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.avgSalary = avgSalary == null ? 0.0 : avgSalary;
        this.totalSalary = totalSalary == null ? 0.0 : totalSalary;
    }

    public Integer getDepartmentId() { return departmentId; }
    public String getDepartmentName() { return departmentName; }
    public double getAvgSalary() { return avgSalary; }
    public double getTotalSalary() { return totalSalary; }
}
