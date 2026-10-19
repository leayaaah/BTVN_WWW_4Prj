package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.Position;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PositionRepository extends MongoRepository<Position, Integer> {
}
