package com.app.backend.controllers;

import com.app.backend.dto.UserDTO;
import com.app.backend.model.User;
import com.app.backend.repository.UserRepository;
import com.app.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class AuthController {

    @Autowired
    private UserService userService;



    @GetMapping("/info")
    public ResponseEntity<UserDTO> getUserInfo(@AuthenticationPrincipal OAuth2User user){
        String email = user.getAttribute("email");
        String name = user.getAttribute("name");
        String phone = user.getAttribute("phone");

        Optional<User> userExist = userService.getByEmail(email);

        if(userExist.isPresent()){

            User info = userExist.get();

            UserDTO userInfo = new UserDTO(info.getName(), info.getEmail(),info.getPhoneNumber());


            return ResponseEntity.ok(userInfo);
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setPhoneNumber(phone);
        newUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        userService.add(newUser);
        UserDTO userCreated = new UserDTO(newUser.getName(), newUser.getEmail(), newUser.getPhoneNumber());

        return ResponseEntity.ok(userCreated);

        }
}
