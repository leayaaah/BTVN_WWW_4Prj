package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.DepartmentEmployeeCount;
import com.example.employeemanagement.dto.DepartmentSalaryStat;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final MongoTemplate mongoTemplate;
    private final EmployeeEnricher enricher;

    public ReportServiceImpl(EmployeeRepository employeeRepository,
                             DepartmentRepository departmentRepository,
                             MongoTemplate mongoTemplate,
                             EmployeeEnricher enricher) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.mongoTemplate = mongoTemplate;
        this.enricher = enricher;
    }

    @Override
    public List<Employee> findByPosition(Integer positionId) {
        return enricher.enrich(new ArrayList<>(employeeRepository.findByPositionId(positionId)));
    }

    @Override
    public List<Employee> findByDepartmentAndPosition(Integer departmentId, Integer positionId) {
        return enricher.enrich(new ArrayList<>(
                employeeRepository.findByDepartmentIdAndPositionId(departmentId, positionId)));
    }

    @Override
    public List<DepartmentEmployeeCount> countEmployeesByDepartment() {
        Map<Integer, DepartmentAggregate> aggregates = aggregateByDepartment();
        List<DepartmentEmployeeCount> result = new ArrayList<>();
        for (Department d : departmentRepository.findAll(Sort.by("id"))) {
            DepartmentAggregate a = aggregates.get(d.getId());
            result.add(new DepartmentEmployeeCount(d.getId(), d.getName(), a == null ? 0L : a.getEmployeeCount()));
        }
        return result;
    }

    @Override
    public List<DepartmentSalaryStat> salaryStatsByDepartment() {
        Map<Integer, DepartmentAggregate> aggregates = aggregateByDepartment();
        List<DepartmentSalaryStat> result = new ArrayList<>();
        for (Department d : departmentRepository.findAll(Sort.by("id"))) {
            DepartmentAggregate a = aggregates.get(d.getId());
            result.add(new DepartmentSalaryStat(d.getId(), d.getName(),
                    a == null ? 0.0 : a.getAvgSalary(), a == null ? 0.0 : a.getTotalSalary()));
        }
        return result;
    }

    @Override
    public List<Employee> topPaidInDepartment(Integer departmentId, int n) {
        return enricher.enrich(new ArrayList<>(
                employeeRepository.findByDepartmentIdOrderBySalaryDesc(departmentId, PageRequest.of(0, n))));
    }

    @Override
    public List<Department> departmentsWithoutEmployees() {
        // Các departmentId đang có nhân viên -> Department có id không nằm trong tập này
        List<Integer> usedIds = mongoTemplate.findDistinct(new Query(), "departmentId", Employee.class, Integer.class);
        return departmentRepository.findByIdNotIn(usedIds);
    }

    /** Aggregation: group theo departmentId -> count, sum, avg (tương đương GROUP BY trong SQL). */
    private Map<Integer, DepartmentAggregate> aggregateByDepartment() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("departmentId")
                        .count().as("employeeCount")
                        .sum("salary").as("totalSalary")
                        .avg("salary").as("avgSalary"));
        List<DepartmentAggregate> rows =
                mongoTemplate.aggregate(aggregation, "employees", DepartmentAggregate.class).getMappedResults();
        Map<Integer, DepartmentAggregate> map = new HashMap<>();
        for (DepartmentAggregate row : rows) {
            map.put(row.getId(), row);
        }
        return map;
    }

    /** Kết quả của $group: _id = departmentId. */
    public static class DepartmentAggregate {
        private Integer id;
        private long employeeCount;
        private double totalSalary;
        private double avgSalary;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public long getEmployeeCount() { return employeeCount; }
        public void setEmployeeCount(long employeeCount) { this.employeeCount = employeeCount; }

        public double getTotalSalary() { return totalSalary; }
        public void setTotalSalary(double totalSalary) { this.totalSalary = totalSalary; }

        public double getAvgSalary() { return avgSalary; }
        public void setAvgSalary(double avgSalary) { this.avgSalary = avgSalary; }
    }
}
