package com.app.backend.repository;
import com.app.backend.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer> {

    // Query optimizada para verificación de propiedad
    boolean existsByIdAndOwnerEmail(int id, String email);

    //Obtener carros de un usuario mediante el id
    List<Car> findByOwnerId(int userId);
    // Query para obtener autos de un usuario
    List<Car> findByOwnerEmail(String email);
}
