package com.app.backend.service;

import com.app.backend.dto.CarDTO;
import com.app.backend.model.Car;
import com.app.backend.model.User;
import com.app.backend.repository.CarRepository;
import com.app.backend.repository.UserRepository;
import com.app.backend.utils.AppwriteUploader;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


//Methods
@Service
@Transactional
public class CarService {

    private final CarRepository carRepository;
    private final UserService userService;
    private final UserRepository userRepository;



    public CarService(CarRepository carRepository, UserService userService, UserRepository userRepository) {
        this.carRepository = carRepository;
        this.userService = userService;
        this.userRepository = userRepository;

    }

    @Value("${appwrite.project.id}")
    private  String PROJECT_ID;

    @Value("${appwrite.api.key}")
    private  String API_KEY;

    @Value("${appwrite.bucket.id}")
    private  String BUCKET_ID;

    private static HttpRequest.BodyPublisher ofMimeMultipartData(String filePath, String fileName, String fileId) throws IOException {
        var boundary = "----JavaMultipartBoundary";
        var byteArrays = new ArrayList<byte[]>();

        // Add fileId field
        byteArrays.add(("--" + boundary + "\r\n").getBytes());
        byteArrays.add("Content-Disposition: form-data; name=\"fileId\"\r\n\r\n".getBytes());
        byteArrays.add(fileId.getBytes());
        byteArrays.add("\r\n".getBytes());

        // Add file field
        byteArrays.add(("--" + boundary + "\r\n").getBytes());
        byteArrays.add(("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n").getBytes());
        byteArrays.add(("Content-Type: image/png\r\n\r\n").getBytes());
        byteArrays.add(Files.readAllBytes(Path.of(filePath)));
        byteArrays.add(("\r\n--" + boundary + "--\r\n").getBytes());

        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
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



    public CarDTO createCar(CarDTO dto, MultipartFile imageFile) throws Exception {

        Optional<User> userOpt = userRepository.findById(dto.getOwnerId());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found.");
        }

        User user = userOpt.get();

        // Guardar temporalmente el archivo
        String originalFileName = imageFile.getOriginalFilename();
        String localDir = System.getProperty("user.dir") + "/car_images";
        String fileName = "car_" + user.getId() + "_" + System.currentTimeMillis() + "_" + originalFileName;

        File folder = new File(localDir);
        if (!folder.exists()) {
            folder.mkdirs();  // This ensures the directory exists
        }

        File localFile = new File(localDir + "/" + fileName);
        imageFile.transferTo(localFile);


        // Subir a Appwrite
        HttpClient httpClient = HttpClient.newHttpClient();
        String fileId = UUID.randomUUID().toString();
        HttpRequest.BodyPublisher bodyPublisher = ofMimeMultipartData(localFile.getPath(), fileName, fileId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://cloud.appwrite.io/v1/storage/buckets/" + BUCKET_ID + "/files"))
                .header("X-Appwrite-Project", PROJECT_ID)
                .header("X-Appwrite-Key", API_KEY)
                .header("Content-Type", "multipart/form-data; boundary=----JavaMultipartBoundary")
                .POST(bodyPublisher)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Appwrite upload response: " + response.body());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Error al subir imagen del coche: " + response.statusCode() + " - " + response.body());
        }

        // Crear URL pública
        String imageUrl = "https://cloud.appwrite.io/v1/storage/buckets/" + BUCKET_ID + "/files/" + fileId + "/view?project=" + PROJECT_ID;

        // Crear entidad Car
        Car car = new Car();
        car.setOwner(user);
        car.setModel(dto.getModel());
        car.setCarPlates(dto.getCarPlates());
        car.setImageUrl(imageUrl);

        Car saved = carRepository.save(car);

        CarDTO responseDto = new CarDTO();
        responseDto.setOwnerId(saved.getOwner().getId());
        responseDto.setModel(saved.getModel());
        responseDto.setCarPlates(saved.getCarPlates());
        responseDto.setImage(saved.getImageUrl());

        return responseDto;
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
