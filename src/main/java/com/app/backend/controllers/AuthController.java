package com.app.backend.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import com.app.backend.utils.JwtUtils;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RestTemplate restTemplate;
    private final JwtUtils jwtUtils;

    @Value("${appwrite.project.id}")
    private String appwriteProjectId;

    @Value("${appwrite.endpoint}")
    private String appwriteEndpoint;

    public AuthController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.restTemplate = new RestTemplate();
    }

    @PostMapping("/appwrite")
    public ResponseEntity<?> authenticateWithAppwrite(@RequestHeader("X-Appwrite-Session") String sessionToken) {
        System.out.println("Received Session Token: " + sessionToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Appwrite-Project", appwriteProjectId);
        headers.add("X-Appwrite-JWT", sessionToken); // Asegurar que se usa el session token correcto

        try {
            // Llamada a Appwrite para obtener los datos del usuario autenticado
            ResponseEntity<Map> response = restTemplate.exchange(
                    appwriteEndpoint + "/account",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class
            );

            Map userData = response.getBody();
            if (userData == null || !userData.containsKey("email")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No se pudo obtener el usuario de Appwrite");
            }

            String email = (String) userData.get("email");
            String jwt = jwtUtils.generateToken(email); // Generar JWT con email

            return ResponseEntity.ok(Map.of("token", jwt));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error al autenticar: " + e.getMessage());
        }
    }
}
