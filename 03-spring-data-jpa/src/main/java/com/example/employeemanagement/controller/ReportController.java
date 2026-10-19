package com.example.employeemanagement.controller;

import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Position;
import com.example.employeemanagement.service.EmployeeService;
import com.example.employeemanagement.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Các chức năng hiển thị thông tin theo quan hệ 1 - n (Department 1-n Employee, Position 1-n Employee).
 */
@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final EmployeeService employeeService;

    public ReportController(ReportService reportService, EmployeeService employeeService) {
        this.reportService = reportService;
        this.employeeService = employeeService;
    }

    @ModelAttribute("departments")
    public List<Department> departments() {
        return employeeService.findAllDepartments();
    }

    @ModelAttribute("positions")
    public List<Position> positions() {
        return employeeService.findAllPositions();
    }

    private void setup(Model model, String title, String action,
                       boolean showDepartment, boolean showPosition, boolean showN) {
        model.addAttribute("title", title);
        model.addAttribute("action", action);
        model.addAttribute("showDepartment", showDepartment);
        model.addAttribute("showPosition", showPosition);
        model.addAttribute("showN", showN);
    }

    // 1. Danh sách Employee theo Position ID
    @GetMapping("/by-position")
    public String byPosition(@RequestParam(required = false) Integer positionId, Model model) {
        setup(model, "Danh sách nhân viên theo chức vụ", "/reports/by-position", false, true, false);
        model.addAttribute("positionId", positionId);
        if (positionId != null) {
            model.addAttribute("employees", reportService.findByPosition(positionId));
        }
        return "reports/employees";
    }

    // 2. Danh sách Employee theo Department ID và Position ID
    @GetMapping("/by-department-position")
    public String byDepartmentAndPosition(@RequestParam(required = false) Integer departmentId,
                                          @RequestParam(required = false) Integer positionId,
                                          Model model) {
        setup(model, "Danh sách nhân viên theo phòng ban và chức vụ",
                "/reports/by-department-position", true, true, false);
        model.addAttribute("departmentId", departmentId);
        model.addAttribute("positionId", positionId);
        if (departmentId != null && positionId != null) {
            model.addAttribute("employees", reportService.findByDepartmentAndPosition(departmentId, positionId));
        }
        return "reports/employees";
    }

    // 3. Thống kê số Employee của mỗi Department
    @GetMapping("/department-count")
    public String departmentCount(Model model) {
        model.addAttribute("stats", reportService.countEmployeesByDepartment());
        return "reports/department-count";
    }

    // 4. Lương trung bình và tổng lương theo Department
    @GetMapping("/department-salary")
    public String departmentSalary(Model model) {
        model.addAttribute("stats", reportService.salaryStatsByDepartment());
        return "reports/department-salary";
    }

    // 5. Top N Employee lương cao nhất trong một Department
    @GetMapping("/top-paid")
    public String topPaid(@RequestParam(required = false) Integer departmentId,
                          @RequestParam(defaultValue = "5") int n,
                          Model model) {
        int limit = Math.max(1, Math.min(n, 100));
        setup(model, "Top nhân viên lương cao nhất trong phòng ban", "/reports/top-paid", true, false, true);
        model.addAttribute("departmentId", departmentId);
        model.addAttribute("n", limit);
        if (departmentId != null) {
            model.addAttribute("employees", reportService.topPaidInDepartment(departmentId, limit));
        }
        return "reports/employees";
    }

    // 6. Danh sách Department chưa có Employee nào
    @GetMapping("/empty-departments")
    public String emptyDepartments(Model model) {
        model.addAttribute("emptyDepartments", reportService.departmentsWithoutEmployees());
        return "reports/empty-departments";
    }
}
