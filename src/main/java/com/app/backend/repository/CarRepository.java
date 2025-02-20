package com.app.backend.repository;
import com.app.backend.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

//CRUD Operations
public interface CarRepository extends JpaRepository<Car, Integer> {



}
