package com.app.backend.controllers;

import com.app.backend.dto.CarDTO;
import com.app.backend.model.Car;
import com.app.backend.model.User;
import com.app.backend.service.CarService;
import com.app.backend.service.UserService;
import com.app.backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/car")
public class CarControllers {

    @Autowired
    private CarService carService;

    @Autowired
    private UserService userService;


    @GetMapping("/all")
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }


    @GetMapping("/{carId}")
    public ResponseEntity<?> getCarById(@PathVariable int carId) {
        String userEmail = getAuthenticatedEmail();

        Optional<Car> car = carService.getCarById(carId);

        if (car.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Verificar que el auto pertenezca al usuario
        if (!car.get().getOwner().getEmail().equals(userEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permiso para acceder a este recurso");
        }

        return ResponseEntity.ok(car.get());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCarsByUserId(@PathVariable int userId) {
        String userEmail = getAuthenticatedEmail();

        // Verify that the authenticated user is requesting their own data
        Optional<User> user = userService.getById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        if (!user.get().getEmail().equals(userEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permiso para acceder a estos recursos");
        }

        List<Car> cars = carService.getCarsByUserId(userId);

        if (cars.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron autos para este usuario");
        }

        return ResponseEntity.ok(cars);
    }



    @PostMapping
    public ResponseEntity<?> createCar(@RequestBody CarDTO carDTO) {
        try {

            User owner = userService.getById(carDTO.getOwnerId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Usuario no encontrado"));


            Car car = new Car();
            car.setCarPlates(carDTO.getCarPlates());
            car.setModel(carDTO.getModel());
            car.setOwner(owner);

            Car savedCar = carService.saveCar(car);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedCar);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al crear auto: " + e.getMessage());
        }
    }

    @DeleteMapping("/{carId}")
    public ResponseEntity<?> deleteCar(@PathVariable int carId) {
        String userEmail = getAuthenticatedEmail();

        if (!carService.isCarOwnedBy(carId, userEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No puedes eliminar un auto que no te pertenece");
        }

        carService.deleteCar(carId);
        return ResponseEntity.noContent().build();
    }

    // Método helper para obtener el email del JWT
    private String getAuthenticatedEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}