package com.app.backend.controllers;

import com.app.backend.dto.UserDTO;
import com.app.backend.model.User;
import com.app.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserControllers {

    @Autowired
    private UserService userService;

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo() {
        // 1. Obtener el email del JWT actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // El email está como "subject" en el JWT

        // 2. Buscar usuario en la base de datos
        Optional<User> userExist = userService.getByEmail(email);

        // 3. Si existe, devolver sus datos
        if(userExist.isPresent()) {
            User info = userExist.get();
            UserDTO userInfo = new UserDTO(
                    info.getId(),
                    info.getName(),
                    info.getEmail(),
                    info.getPhoneNumber()
            );
            return ResponseEntity.ok(userInfo);
        }

        // 4. Si no existe, crear nuevo usuario
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setName(""); // Puedes dejarlo vacío o pedirlo después
        newUser.setPhoneNumber(""); // Opcional
        newUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        userService.add(newUser);

        UserDTO userCreated = new UserDTO(
                newUser.getId(),
                newUser.getName(),
                newUser.getEmail(),
                newUser.getPhoneNumber()
        );

        return ResponseEntity.ok(userCreated);
    }
}
