package com.app.backend.controllers;

import com.app.backend.dto.CarDTO;
import com.app.backend.model.Car;
import com.app.backend.model.User;
import com.app.backend.service.CarService;
import com.app.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
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


        return ResponseEntity.ok(cars);
    }



    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CarDTO> createCar(
            @RequestPart("car") String carJson,
            @RequestPart("image") MultipartFile imageFile) {
        try {
            System.out.println("Received car JSON: " + carJson);
            // Deserialize the JSON string to CarDTO
            CarDTO carDTO = new ObjectMapper().readValue(carJson, CarDTO.class);
            CarDTO createdCar = carService.createCar(carDTO, imageFile);
            return ResponseEntity.ok(createdCar);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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