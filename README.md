# Bài tập Spring Data (tuần 05-06-07) – Quản lý nhân viên

Mỗi kỹ thuật là **một project Maven riêng** (Spring Boot 3.3.5, JDK 21, Tomcat 10 nhúng, Thymeleaf, kiến trúc MVC):

| Thư mục | Kỹ thuật | Database | Cổng |
|---|---|---|---|
| `01-spring-jdbc` | Spring JDBC – `JdbcTemplate` + `RowMapper` | SQL `employeedb` | 8081 |
| `02-spring-data-jdbc` | Spring Data JDBC – `ListCrudRepository` (`@Table`, `@Id`) | SQL `employeedb` | 8082 |
| `03-spring-data-jpa` | Spring Data JPA – `JpaRepository` (`@Entity`, `@ManyToOne`) | SQL `employeedb` | 8083 |
| `04-spring-data-mongodb` | Spring Data MongoDB – `MongoRepository`, `MongoTemplate` (`@Document`) | NoSQL `employeewww` | 8084 |

## Chuẩn bị database
**SQL (project 01, 02, 03):** import `db/employeedb.sql` vào MariaDB/MySQL (HeidiSQL: *File > Run SQL file*).
Sửa `spring.datasource.username/password` trong `application.properties` cho đúng máy (mặc định `root` / `root`).

**NoSQL (project 04):** chỉ cần MongoDB đang chạy ở `localhost:27017`. Lần chạy đầu, lớp `DataLoader`
tự nạp `departments.json` + `positions.json` (nằm trong `src/main/resources/data`) vào database `employeewww`,
tách thành 3 collection `positions`, `departments`, `employees`.

## Chạy
Mở từng thư mục (chứa `pom.xml`) bằng IntelliJ -> *Open as Project* -> chạy `EmployeeManagementApplication`,
rồi vào `http://localhost:<cổng>/employees`.

## Chức năng
* CRUD Employee: `/employees`, `/employees/new`, `/employees/{id}`, `/employees/{id}/edit`, xóa (POST).
* Tìm kiếm (kết hợp được nhiều điều kiện): `/employees/search` – theo id, name, tuổi (từ – đến), lương (lớn hơn), chức vụ.
* Quan hệ 1 – n (`/reports/...`):
  * `by-position` – Employee theo Position ID
  * `by-department-position` – Employee theo Department ID + Position ID
  * `department-count` – số Employee của mỗi Department
  * `department-salary` – lương trung bình và tổng lương theo Department
  * `top-paid` – Top N Employee lương cao nhất trong một Department
  * `empty-departments` – Department chưa có Employee nào

## Cách mỗi kỹ thuật cài đặt (đối chiếu bảng trong đề)
| Chức năng | Spring JDBC | Spring Data JDBC | Spring Data JPA | Spring Data MongoDB |
|---|---|---|---|---|
| Thêm/Sửa | `update(INSERT/UPDATE)` | `save` | `save` | `save` (id tự sinh = max + 1) |
| Xóa | `update(DELETE ... WHERE id=?)` | `deleteById` | `deleteById` | `deleteById` |
| Danh sách / theo id | `query` / `queryForObject` | `findAll` / `findById` | `findAll` / `findById` | `findAll` / `findById` |
| Tìm theo name | `LIKE ?` | `findByNameContainingIgnoreCase` | `findByNameContainingIgnoreCase` | `findByNameContainingIgnoreCase` |
| Tìm theo age | `BETWEEN ? AND ?` | `findByAgeBetween` | `findByAgeBetween` | `MongoTemplate` + `Criteria.gte/lte` |
| Tìm theo salary | `salary > ?` | `findBySalaryGreaterThan` | `findBySalaryGreaterThan` | `findBySalaryGreaterThan` |
| Thống kê theo Department | SQL `GROUP BY` + RowMapper | `@Query` + `rowMapperClass` | `@Query` JPQL + DTO | `Aggregation.group` |
| Top N | `ORDER BY salary DESC LIMIT ?` | `@Query ... LIMIT :limit` | `...OrderBySalaryDesc(id, Pageable)` | `...OrderBySalaryDesc(id, Pageable)` |
| Department chưa có Employee | `LEFT JOIN ... IS NULL` | `@Query` | `@Query` JPQL | `findDistinct` + `findByIdNotIn` |

## Khi nộp bài
Đề yêu cầu đặt tên `MaSV_HoTen_Tuan06` và nén `.zip`/`.rar`: đổi tên thư mục gốc `MaSV_HoTen_Tuan06` cho đúng mã số sinh viên và họ tên của bạn.
