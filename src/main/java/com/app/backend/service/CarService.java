package com.app.backend.service;

import com.app.backend.model.Car;
import com.app.backend.repository.CarRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


//Methods
@Service
@Transactional
public class CarService {

    private final CarRepository carRepository;
    private final UserService userService;

    public CarService(CarRepository carRepository, UserService userService) {
        this.carRepository = carRepository;
        this.userService = userService;
    }


    public List<Car> getAllCars() {
        return carRepository.findAll();
    }


    public Optional<Car> getCarById(int carId) {
        return carRepository.findById(carId);
    }

    public List<Car> getCarsByUserId(int userId) {
        return carRepository.findByOwnerId(userId);
    }



    public boolean isCarOwnedBy(int carId, String userEmail) {
        return carRepository.existsByIdAndOwnerEmail(carId, userEmail);
    }


    public Car saveCar(Car car) {
        if (car.getOwner() == null || car.getOwner().getEmail() == null) {
            throw new IllegalStateException("El auto debe tener un dueño válido");
        }
        return carRepository.save(car);
    }


    public void deleteCar(int carId) {
        if (!carRepository.existsById(carId)) {
            throw new EntityNotFoundException("Auto no encontrado con ID: " + carId);
        }
        carRepository.deleteById(carId);
    }


    public List<Car> getCarsByUserEmail(String email) {
        return carRepository.findByOwnerEmail(email);
    }
}
