package com.app.backend.service;

import com.app.backend.dto.ReservationDTO;
import com.app.backend.model.*;
import com.app.backend.repository.CarRepository;
import com.app.backend.repository.ParkingSpotRepository;
import com.app.backend.repository.ReservationRepository;
import com.app.backend.repository.UserRepository;
import com.app.backend.utils.QRCodeUtil;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Value("${appwrite.project.id}")
    private  String PROJECT_ID;

    @Value("${appwrite.api.key}")
    private  String API_KEY;

    @Value("${appwrite.bucket.id}")
    private  String BUCKET_ID;

    public class VerificationResult {
        private boolean success;
        private String message;

        public VerificationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }



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



    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    CarRepository carRepository;

    @Autowired
    private PenaltyService penaltyService;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);


    public ReservationDTO createReservation(ReservationDTO dto) throws Exception {



        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        Optional<ParkingSpot> spotOpt = parkingSpotRepository.findById(dto.getSpotId());
        Optional<Car> userCarOpt = carRepository.findById(dto.getUserCarId());

        if (userOpt.isEmpty() || spotOpt.isEmpty()) {
            throw new RuntimeException("User or parking spot not found.");
        }
        if (userCarOpt.isEmpty()) {
            throw new RuntimeException("User car not found.");
        }

        User user = userOpt.get();
        ParkingSpot spot = spotOpt.get();
        Car userCar = userCarOpt.get();

        boolean hasActive = reservationRepository.existsByUserIdAndStatus(userOpt.get().getId(), Reservations.ReservationsStatus.ACTIVE);

        if(hasActive){
            throw new IllegalStateException("User already has an active reservation");
        }

        // Check if the spot is already reserved during the requested time
        boolean isReserved = reservationRepository.existsBySpotAndStatusAndStartTimeBeforeAndEndTimeAfter(
                spot, Reservations.ReservationsStatus.ACTIVE, dto.getEndTime(), dto.getStartTime());

        if (isReserved) {
            throw new RuntimeException("Parking spot is already reserved for the selected time.");
        }



        //Change spot status
        spot.setStatus(ParkingSpot.ParkingStatus.RESERVED);

        // Create reservation
        Reservations reservation = new Reservations();
        reservation.setUser(user);
        reservation.setSpot(spot);
        reservation.setStartTime(dto.getStartTime());
        reservation.setEndTime(dto.getEndTime());
        reservation.setStatus(Reservations.ReservationsStatus.ACTIVE);
        reservation.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        reservation.setUserCar(userCar);

        // Generate QR code image
        String qrCodeText = "UserID: " + user.getId() +
                "\nSpot Number: " + spot.getId() +
                "\nStart Time: " + dto.getStartTime() +
                "\nEnd Time: " + dto.getEndTime();

        String fileName = "reservation_" + user.getId() + "_" + System.currentTimeMillis() + ".png";
        String localPath = "qrcodes/" + fileName;

        // Ensure folder exists
        File folder = new File("qrcodes");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Generate QR image
        QRCodeUtil.generateQRCodeImage(qrCodeText, 350, 350, localPath);

        // Upload QR image to Appwrite
        HttpClient httpClient = HttpClient.newHttpClient();
        String fileId = UUID.randomUUID().toString();
        HttpRequest.BodyPublisher bodyPublisher = ofMimeMultipartData(localPath, fileName, fileId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://cloud.appwrite.io/v1/storage/buckets/" + BUCKET_ID + "/files"))
                .header("X-Appwrite-Project", PROJECT_ID)
                .header("X-Appwrite-Key", API_KEY)
                .header("Content-Type", "multipart/form-data; boundary=----JavaMultipartBoundary")
                .POST(bodyPublisher)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Appwrite upload response: " + response.body());


        // Generate the Appwrite public URL (you must enable file preview or access)
        String fileUrl = "https://cloud.appwrite.io/v1/storage/buckets/" + BUCKET_ID + "/files/" + fileId + "/view?project=" + PROJECT_ID;
        reservation.setQrCodeUrl(fileUrl);

        // Save reservation
        Reservations saved = reservationRepository.save(reservation);


        ReservationDTO responseDTO = new ReservationDTO();
        responseDTO.setReservationId(saved.getReservation_id());
        responseDTO.setUserId(saved.getUser().getId());
        responseDTO.setSpotId(saved.getSpot().getId());
        responseDTO.setStartTime(saved.getStartTime());
        responseDTO.setEndTime(saved.getEndTime());
        responseDTO.setQrCodeUrl(saved.getQrCodeUrl());
        responseDTO.setUserCarId(saved.getUserCar().getId());

        return responseDTO;

    }

    public ReservationDTO getReservationById(int id) {
        Optional<Reservations> resOpt = reservationRepository.findById(id);
        if (resOpt.isEmpty()) {
            throw new RuntimeException("Reservation not found");
        }

        Reservations res = resOpt.get();
        ReservationDTO dto = new ReservationDTO();
        dto.setReservationId(res.getReservation_id());
        dto.setUserId(res.getUser().getId());
        dto.setSpotId(res.getSpot().getId());
        dto.setStartTime(res.getStartTime());
        dto.setEndTime(res.getEndTime());
        dto.setQrCodeUrl(res.getQrCodeUrl());
        dto.setUserCarId(res.getUserCar().getId());
        dto.setCarModel(res.getUserCar().getModel());
        dto.setCarPlates(res.getUserCar().getCarPlates());
        dto.setSpotSection(res.getSpot().getSection());
        dto.setCreatedAt(res.getCreatedAt());
        dto.setStatus(res.getStatus().name());
        dto.setCreatedAt(res.getCreatedAt());

        return dto;
    }

    public List<ReservationDTO> getAllReservationsByUserId(int userId) {
        List<Reservations> reservations = reservationRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return reservations.stream().map(res -> {
            ReservationDTO dto = new ReservationDTO();
            dto.setReservationId(res.getReservation_id());
            dto.setUserId(res.getUser().getId());
            dto.setSpotId(res.getSpot().getId());
            dto.setStartTime(res.getStartTime());
            dto.setEndTime(res.getEndTime());
            dto.setQrCodeUrl(res.getQrCodeUrl());
            dto.setUserCarId(res.getUserCar().getId());
            dto.setCarModel(res.getUserCar().getModel());
            dto.setCarPlates(res.getUserCar().getCarPlates());
            dto.setSpotSection(res.getSpot().getSection());
            dto.setStatus(res.getStatus().name());
            return dto;
        }).collect(Collectors.toList());
    }


    public String cancelReservation(int reservationId, int userId) {
        Optional<Reservations> reservationOpt = reservationRepository.findById(reservationId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (reservationOpt.isEmpty() || userOpt.isEmpty()) {
            return "Reservation or user not found.";
        }

        Reservations reservation = reservationOpt.get();
        User user = userOpt.get();


        if (reservation.getUser().getId() != userId) {
            return "You are not allowed to cancel this reservation.";
        }

        if (reservation.getStatus() == Reservations.ReservationsStatus.CANCELLED ||
                reservation.getStatus() == Reservations.ReservationsStatus.COMPLETED) {
            return "Reservation is already finalized.";
        }


        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp startTime = reservation.getCreatedAt();


        reservation.setStatus(Reservations.ReservationsStatus.CANCELLED);
        reservationRepository.save(reservation);

        ParkingSpot spot = reservation.getSpot();
        spot.setStatus(ParkingSpot.ParkingStatus.AVAILABLE);
        parkingSpotRepository.save(spot);

        // Si cancela 5+ min después del inicio, aplicar penalización
        if (now.after(Timestamp.from(startTime.toInstant().plus(5, ChronoUnit.MINUTES)))) {
            penaltyService.createPenalty(
                    user,
                    reservation,
                    new BigDecimal("30.00"),
                    Penalty.Reason.CANCELLED
            );
            return "Reservation cancelled — late cancel penalty applied.";
        }

        return "Reservation cancelled successfully.";
    }

    public Optional<Reservations> getActiveReservation(int userId) {
        return reservationRepository.findByUserIdAndStatus(userId, Reservations.ReservationsStatus.ACTIVE);
    }

    public void completeReservation(Integer reservationId) {
        Reservations reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(Reservations.ReservationsStatus.COMPLETED);

        ParkingSpot spot = reservation.getSpot();
        spot.setStatus(ParkingSpot.ParkingStatus.AVAILABLE);

        parkingSpotRepository.save(spot);
        reservationRepository.save(reservation);
    }

    public void markUserArrival(Integer reservationId) {
        Reservations reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));



        // Set status on reservation
        reservation.setStatus(Reservations.ReservationsStatus.ARRIVE);

        // Set status on parking spot
        ParkingSpot spot = reservation.getSpot();
        spot.setStatus(ParkingSpot.ParkingStatus.OCCUPIED);

        // Save changes
        reservationRepository.save(reservation);
        parkingSpotRepository.save(spot);
    }

    public void extendReservation(Integer reservationId, Timestamp newEndTime) {
        Reservations reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (reservation.getStatus() != Reservations.ReservationsStatus.ARRIVE) {
            throw new IllegalArgumentException("Only ARRIVE reservations can be extended.");
        }

        if (newEndTime.before(reservation.getEndTime())) {
            throw new IllegalArgumentException("New end time must be after current end time.");
        }

        // Validar que no haya conflicto con otras reservas para el mismo spot
        boolean hasConflict = reservationRepository.existsBySpotAndStatusAndStartTimeBeforeAndEndTimeAfter(
                reservation.getSpot(), Reservations.ReservationsStatus.ACTIVE, newEndTime, reservation.getEndTime());

        if (hasConflict) {
            throw new IllegalArgumentException("Cannot extend reservation: time conflict with another reservation.");
        }

        reservation.setEndTime(newEndTime);
        reservationRepository.save(reservation);
    }



    public VerificationResult verifyQRCode(int reservationId, int userId) {
        Optional<Reservations> reservationOpt = reservationRepository.findById(reservationId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (reservationOpt.isEmpty() || userOpt.isEmpty()) {
            return new VerificationResult(false, "Reservation or user not found.");
        }

        Reservations reservation = reservationOpt.get();
        User user = userOpt.get();

        //Unauthorized usage
        if (reservation.getUser().getId() != userId) {
            penaltyService.createPenalty(
                    user,
                    reservation,
                    new BigDecimal("100.00"),
                    Penalty.Reason.UNAUTHORIZED_USAGE
            );
            return new VerificationResult(false, "Unauthorized usage — penalty applied.");
        }

        // Late arrival check (after 10 min of start time)
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (now.after(Timestamp.from(reservation.getStartTime().toInstant().plus(15, ChronoUnit.MINUTES)))) {
            penaltyService.createPenalty(
                    user,
                    reservation,
                    new BigDecimal("50.00"),
                    Penalty.Reason.LATE_ARRIVAL
            );

            ParkingSpot spot = reservation.getSpot();
            spot.setStatus(ParkingSpot.ParkingStatus.AVAILABLE);
            parkingSpotRepository.save(spot);
            return new VerificationResult(false, "Late arrival — penalty applied.");
        }

        // ✅ Valid usage
        ParkingSpot spot = reservation.getSpot();
        spot.setStatus(ParkingSpot.ParkingStatus.OCCUPIED);
        parkingSpotRepository.save(spot);
        return new VerificationResult(true, "Reservation is valid!");
    }




}
