package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Integer> {
}
