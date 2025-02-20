package com.app.backend.controllers;

import com.app.backend.dto.CarDTO;
import com.app.backend.model.Car;
import com.app.backend.model.User;
import com.app.backend.service.CarService;
import com.app.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//Endpoints
@RestController
@RequestMapping("/api/car")
public class CarControllers {

    @Autowired
    private CarService carService;


    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public List<Car> getAllCars() {
        return carService.getAllCars();
    }

    @GetMapping("/get/{car_id}")
    public Optional<Car> getById(@AuthenticationPrincipal OAuth2User user, @PathVariable int car_id){
        return carService.getCarInfo(car_id);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCar(@AuthenticationPrincipal OAuth2User user, @RequestBody CarDTO car){

        if(user == null){
            return ResponseEntity.status(401).body("Unauthorized Access");
        }


        User owner = userService.getById(car.getOwnerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Car new_car = new Car();
        new_car.setCarPlates(car.getCarPlates());
        new_car.setModel(car.getModel());
        new_car.setOwner(owner);

        Car savedCar = carService.addCar(new_car);

        System.out.println("Car saved correctly");

        return ResponseEntity.ok(savedCar);

    }

    

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCar(@AuthenticationPrincipal OAuth2User user, @PathVariable int id){

        if(user == null){
            return ResponseEntity.status(401).body("Unauthorized Access");
        }

        boolean deleted = carService.deleteCar(id);


        if(deleted){
            return ResponseEntity.ok("Car deleted");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car not found");
        }

    }






}
