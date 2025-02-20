package com.app.backend.service;

import com.app.backend.model.Car;
import com.app.backend.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


//Methods
@Service
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository){
        this.carRepository = carRepository;
    }

    public List<Car> getAllCars(){
        return carRepository.findAll();
    }

    public Optional<Car> getCarInfo(int id){
        return carRepository.findById(id);
    }
    public Car addCar(Car car){
        return carRepository.save(car);
    }

    public boolean deleteCar(int id) {
        Optional<Car> carOptional = carRepository.findById(id);

        if (carOptional.isPresent()) {
            carRepository.delete(carOptional.get());
            return true; // Indicates successful deletion
        } else {
            return false; // Car not found
        }
    }



}
