package com.app.backend.service;

import com.app.backend.model.User;
import com.app.backend.repository.CarRepository;
import com.app.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CarRepository carRepository;

    @Autowired
    public UserService(UserRepository userRepository, CarRepository carRepository){
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    public User add(User user){
        return userRepository.save(user);
    }

    public Optional<User> getById(int id_owner){
        return userRepository.findById(id_owner);
    }

    public List<User> getAllOwners(){
        return userRepository.findAll();
    }


    public Optional<User> getByEmail(String email){
        return userRepository.findByEmail(email);
    }




}
